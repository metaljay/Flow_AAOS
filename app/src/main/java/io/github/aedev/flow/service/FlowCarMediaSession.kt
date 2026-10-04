package io.github.aedev.flow.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import io.github.aedev.flow.R
import io.github.aedev.flow.player.EnhancedMusicPlayerManager
import io.github.aedev.flow.player.EnhancedPlayerManager
import io.github.aedev.flow.player.GlobalPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.abs

/**
 * AAOS fork: the session the car's home screen media card reads.
 *
 * AAOS only treats apps with a MediaBrowserService as media sources, the card reads the session token
 * that service hands out, and the Google car launcher skips a service of an app with a launcher
 * activity unless it opts in with `androidx.car.app.launchable`. Flow plays videos and music in two
 * separate Media3 sessions, so this single framework session mirrors whichever one played last and
 * forwards the car's controls to it. The last item is saved to disk so the card can be refilled,
 * without network, after the app process has been closed.
 */
object FlowCarMediaSession {
    private const val TAG = "FlowCarMediaSession"
    private const val PREFS_NAME = "flow_car_media_card"
    private const val KEY_KIND = "kind"
    private const val KEY_TITLE = "title"
    private const val KEY_SUBTITLE = "subtitle"
    private const val KEY_ARTWORK_URL = "artwork_url"
    private const val KEY_POSITION_MS = "position_ms"
    private const val KEY_DURATION_MS = "duration_ms"
    private const val ARTWORK_FILE_NAME = "car_media_card_artwork.png"
    private const val POSITION_SAVE_INTERVAL_MS = 5_000L
    private const val OPEN_APP_PROMPT_MS = 15_000L
    private const val MAX_ARTWORK_EDGE_PX = 512

    // Car.CAR_EXTRA_BROWSE_SERVICE_FOR_SESSION: tells CarMediaService which browse service owns this session.
    private const val CAR_EXTRA_BROWSE_SERVICE_FOR_SESSION = "android.media.session.BROWSE_SERVICE"

    // androidx.media.utils.MediaConstants: AAOS shows a button that launches this PendingIntent.
    private const val EXTRA_ERROR_RESOLUTION_ACTION_LABEL = "android.media.extras.ERROR_RESOLUTION_ACTION_LABEL"
    private const val EXTRA_ERROR_RESOLUTION_ACTION_INTENT = "android.media.extras.ERROR_RESOLUTION_ACTION_INTENT"

    const val LAST_PLAYED_MEDIA_ID = "flow_last_played"
    const val CONTINUE_ID = "flow_continue"

    private enum class Kind { VIDEO, MUSIC }

    private data class Item(
        val kind: Kind,
        val title: String,
        val subtitle: String?,
        val artworkUrl: String?,
    )

    private data class Playback(
        val isPlaying: Boolean,
        val isBuffering: Boolean,
        val positionMs: Long,
        val durationMs: Long,
    )

    private val mainHandler = Handler(Looper.getMainLooper())
    private val promptToken = Any()
    private var session: MediaSession? = null
    private var appContext: Context? = null
    private var observing = false
    private var item: Item? = null
    private var playback = Playback(isPlaying = false, isBuffering = false, positionMs = 0L, durationMs = 0L)
    private var lastSavedPositionMs = Long.MIN_VALUE

    /** Called from Application.onCreate: mirrors both players into the car session. */
    fun startObserving(context: Context) {
        if (observing) return
        observing = true
        val ctx = context.applicationContext
        appContext = ctx
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        scope.launch {
            combine(GlobalPlayerState.currentVideo, EnhancedPlayerManager.getInstance().playerState) { video, state ->
                video to state
            }.collect { (video, state) ->
                if (video == null) return@collect
                val manager = EnhancedPlayerManager.getInstance()
                onPlayerUpdate(
                    ctx,
                    Item(Kind.VIDEO, video.title, video.channelName.takeIf(String::isNotBlank), video.thumbnailUrl),
                    Playback(
                        isPlaying = state.isPlaying,
                        isBuffering = state.isBuffering,
                        positionMs = manager.getCurrentPosition(),
                        durationMs = manager.getDuration().takeIf { it > 0L } ?: (video.duration * 1000L),
                    ),
                )
            }
        }
        scope.launch {
            combine(EnhancedMusicPlayerManager.currentTrack, EnhancedMusicPlayerManager.playerState) { track, state ->
                track to state
            }.collect { (track, state) ->
                if (track == null) return@collect
                onPlayerUpdate(
                    ctx,
                    Item(Kind.MUSIC, track.title, track.artist.takeIf(String::isNotBlank), track.thumbnailUrl),
                    Playback(
                        isPlaying = state.isPlaying,
                        isBuffering = state.isBuffering,
                        positionMs = state.position,
                        durationMs = state.duration.takeIf { it > 0L } ?: (track.duration * 1000L),
                    ),
                )
            }
        }
    }

    /** Must be called on the main thread. */
    fun get(context: Context): MediaSession {
        session?.let { return it }
        val ctx = context.applicationContext
        appContext = ctx
        return MediaSession(ctx, TAG).apply {
            setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setExtras(Bundle().apply { putString(CAR_EXTRA_BROWSE_SERVICE_FOR_SESSION, FlowCarMediaBrowserService::class.java.name) })
            launchPendingIntent(ctx, 0)?.let(::setSessionActivity)
            setCallback(callback, mainHandler)
            session = this
        }
    }

    /**
     * Called when the car binds the browse service. If nothing has played in this process yet, refills
     * the session from disk as paused so the card shows the last item.
     */
    fun restoreIfEmpty(context: Context) {
        if (item != null) return
        val prefs = prefs(context)
        val title = prefs.getString(KEY_TITLE, null)?.takeIf(String::isNotBlank) ?: return
        val kind = runCatching { Kind.valueOf(prefs.getString(KEY_KIND, null).orEmpty()) }.getOrDefault(Kind.VIDEO)
        item = Item(kind, title, prefs.getString(KEY_SUBTITLE, null), prefs.getString(KEY_ARTWORK_URL, null))
        playback =
            Playback(
                isPlaying = false,
                isBuffering = false,
                positionMs = prefs.getLong(KEY_POSITION_MS, 0L),
                durationMs = prefs.getLong(KEY_DURATION_MS, 0L),
            )
        publishMetadata(context)
        publishPlaybackState(context)
    }

    /** Root folder so the car's media screen shows a "Continue" tab instead of an empty list. */
    fun continueFolder(context: Context): MediaBrowser.MediaItem =
        MediaBrowser.MediaItem(
            MediaDescription
                .Builder()
                .setMediaId(CONTINUE_ID)
                .setTitle(context.getString(R.string.aaos_car_media_continue))
                .build(),
            MediaBrowser.MediaItem.FLAG_BROWSABLE,
        )

    /** Browse item for the car's media browser and the system's "recent" query. */
    fun lastPlayedBrowseItem(context: Context): MediaBrowser.MediaItem? {
        val prefs = prefs(context)
        val title = item?.title ?: prefs.getString(KEY_TITLE, null)?.takeIf(String::isNotBlank) ?: return null
        val subtitle = item?.subtitle ?: prefs.getString(KEY_SUBTITLE, null)
        val description =
            MediaDescription
                .Builder()
                .setMediaId(LAST_PLAYED_MEDIA_ID)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setIconUri(artworkContentUri(context))
                .build()
        return MediaBrowser.MediaItem(description, MediaBrowser.MediaItem.FLAG_PLAYABLE)
    }

    private fun onPlayerUpdate(
        context: Context,
        nextItem: Item,
        nextPlayback: Playback,
    ) {
        val current = item
        // The car follows whichever player is playing; a paused player only takes over an empty card.
        val takeOver = nextPlayback.isPlaying || current == null || current.kind == nextItem.kind
        if (!takeOver) return

        val itemChanged = current != nextItem
        item = nextItem
        val stateChanged =
            playback.isPlaying != nextPlayback.isPlaying ||
                playback.isBuffering != nextPlayback.isBuffering ||
                playback.durationMs != nextPlayback.durationMs ||
                (!nextPlayback.isPlaying && abs(playback.positionMs - nextPlayback.positionMs) >= 2_000L)
        playback = nextPlayback

        if (itemChanged) {
            saveItem(context, nextItem)
            if (current?.artworkUrl != nextItem.artworkUrl) loadArtwork(context, nextItem.artworkUrl)
            publishMetadata(context)
        }
        savePlayback(context, force = itemChanged || stateChanged)
        if (itemChanged || stateChanged) {
            if (stateChanged && !itemChanged && playback.durationMs > 0L) publishMetadata(context)
            publishPlaybackState(context)
        }
    }

    private val callback =
        object : MediaSession.Callback() {
            override fun onPlay() = handlePlay()

            override fun onPlayFromMediaId(
                mediaId: String?,
                extras: Bundle?,
            ) = handlePlay()

            override fun onPause() {
                when (item?.kind) {
                    Kind.VIDEO -> EnhancedPlayerManager.getInstance().getPlayer()?.pause()
                    Kind.MUSIC -> EnhancedMusicPlayerManager.pause()
                    null -> Unit
                }
            }

            override fun onStop() = onPause()

            override fun onSeekTo(pos: Long) {
                when (item?.kind) {
                    Kind.VIDEO -> EnhancedPlayerManager.getInstance().getPlayer()?.seekTo(pos.coerceAtLeast(0L))
                    Kind.MUSIC -> EnhancedMusicPlayerManager.seekTo(pos.coerceAtLeast(0L))
                    null -> Unit
                }
            }

            override fun onSkipToNext() {
                if (item?.kind == Kind.MUSIC) EnhancedMusicPlayerManager.player?.seekToNext()
            }

            override fun onSkipToPrevious() {
                if (item?.kind == Kind.MUSIC) EnhancedMusicPlayerManager.player?.seekToPrevious()
            }
        }

    private fun handlePlay() {
        val ctx = appContext ?: return
        when (item?.kind) {
            Kind.VIDEO -> {
                val player = EnhancedPlayerManager.getInstance().getPlayer()
                if (player != null && player.mediaItemCount > 0) player.play() else showOpenAppPrompt(ctx)
            }

            Kind.MUSIC -> {
                val player = EnhancedMusicPlayerManager.player
                if (player != null && player.mediaItemCount > 0) EnhancedMusicPlayerManager.play() else resumeMusic(ctx)
            }

            null -> {
                showOpenAppPrompt(ctx)
            }
        }
    }

    /** Music can play without the app open: ask the music service to resume its saved queue. */
    private fun resumeMusic(context: Context) {
        val token = SessionToken(context, ComponentName(context, Media3MusicService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener(
            {
                val controller = runCatching { future.get() }.getOrNull()
                if (controller == null) {
                    showOpenAppPrompt(context)
                    return@addListener
                }
                // An empty player makes Media3 call Media3MusicService.onPlaybackResumption.
                controller.play()
                mainHandler.postDelayed({ controller.release() }, 10_000L)
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    /**
     * Video can only play inside the app, and Android blocks a background app from opening its own
     * activity. Following the AAOS media error guidance, show a message with an "Open Flow" button
     * whose PendingIntent the car UI launches, then fall back to the paused card.
     */
    private fun showOpenAppPrompt(context: Context) {
        val mediaSession = get(context)
        val pendingIntent = launchPendingIntent(context, 1) ?: return
        val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
        mediaSession.setPlaybackState(
            PlaybackState
                .Builder()
                .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PLAY_FROM_MEDIA_ID)
                .setState(PlaybackState.STATE_ERROR, playback.positionMs, 0f, SystemClock.elapsedRealtime())
                .setErrorMessage(context.getString(R.string.aaos_car_media_open_app_message, appName))
                .setExtras(
                    Bundle().apply {
                        putString(EXTRA_ERROR_RESOLUTION_ACTION_LABEL, context.getString(R.string.aaos_car_media_open_app_action, appName))
                        putParcelable(EXTRA_ERROR_RESOLUTION_ACTION_INTENT, pendingIntent)
                    },
                ).build(),
        )
        mainHandler.removeCallbacksAndMessages(promptToken)
        mainHandler.postAtTime(
            {
                if (mediaSession.controller.playbackState?.state == PlaybackState.STATE_ERROR) publishPlaybackState(context)
            },
            promptToken,
            SystemClock.uptimeMillis() + OPEN_APP_PROMPT_MS,
        )
    }

    private fun publishMetadata(context: Context) {
        val current = item ?: return
        val builder =
            MediaMetadata
                .Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, LAST_PLAYED_MEDIA_ID)
                .putString(MediaMetadata.METADATA_KEY_TITLE, current.title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, current.title)
        current.subtitle?.let { subtitle ->
            builder.putString(MediaMetadata.METADATA_KEY_ARTIST, subtitle)
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
        }
        // AAOS only shows artwork from a local content:// URI, so the saved image is served by FlowCarMediaArtworkProvider.
        artworkContentUri(context)?.toString()?.let { uri ->
            builder.putString(MediaMetadata.METADATA_KEY_ART_URI, uri)
            builder.putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, uri)
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, uri)
        }
        if (playback.durationMs > 0L) builder.putLong(MediaMetadata.METADATA_KEY_DURATION, playback.durationMs)
        val mediaSession = get(context)
        runCatching { mediaSession.setMetadata(builder.build()) }
            .onFailure { error -> Log.w(TAG, "Failed to publish metadata", error) }
        mediaSession.isActive = true
    }

    private fun publishPlaybackState(context: Context) {
        val current = playback
        val state =
            when {
                current.isBuffering -> PlaybackState.STATE_BUFFERING
                current.isPlaying -> PlaybackState.STATE_PLAYING
                else -> PlaybackState.STATE_PAUSED
            }
        var actions =
            PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackState.ACTION_SEEK_TO or
                PlaybackState.ACTION_STOP
        if (item?.kind == Kind.MUSIC) {
            actions = actions or PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS
        }
        mainHandler.removeCallbacksAndMessages(promptToken)
        get(context).setPlaybackState(
            PlaybackState
                .Builder()
                .setActions(actions)
                .setState(state, current.positionMs.coerceAtLeast(0L), if (current.isPlaying) 1f else 0f, SystemClock.elapsedRealtime())
                .build(),
        )
    }

    private fun saveItem(
        context: Context,
        next: Item,
    ) {
        val prefs = prefs(context)
        if (prefs.getString(KEY_ARTWORK_URL, null) != next.artworkUrl) {
            runCatching { artworkFile(context).delete() }
        }
        prefs
            .edit()
            .putString(KEY_KIND, next.kind.name)
            .putString(KEY_TITLE, next.title)
            .putString(KEY_SUBTITLE, next.subtitle)
            .putString(KEY_ARTWORK_URL, next.artworkUrl)
            .apply()
        lastSavedPositionMs = Long.MIN_VALUE
    }

    private fun savePlayback(
        context: Context,
        force: Boolean,
    ) {
        val positionMs = playback.positionMs
        if (!force && lastSavedPositionMs != Long.MIN_VALUE && abs(positionMs - lastSavedPositionMs) < POSITION_SAVE_INTERVAL_MS) {
            return
        }
        lastSavedPositionMs = positionMs
        prefs(context)
            .edit()
            .putLong(KEY_POSITION_MS, positionMs.coerceAtLeast(0L))
            .putLong(KEY_DURATION_MS, playback.durationMs.coerceAtLeast(0L))
            .apply()
    }

    private fun loadArtwork(
        context: Context,
        url: String?,
    ) {
        if (url.isNullOrBlank()) return
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = runCatching { downloadArtwork(url) }.getOrNull() ?: return@launch
            runCatching { FileOutputStream(artworkFile(context)).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) } }
            withContext(Dispatchers.Main) {
                if (item?.artworkUrl == url) publishMetadata(context)
            }
        }
    }

    private fun downloadArtwork(url: String): Bitmap? {
        val connection =
            (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 15_000
                instanceFollowRedirects = true
            }
        return try {
            if (connection.responseCode !in 200..299) return null
            val bytes = connection.inputStream.use { it.readBytes() }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            var sampleSize = 1
            while (bounds.outWidth / sampleSize > MAX_ARTWORK_EDGE_PX || bounds.outHeight / sampleSize > MAX_ARTWORK_EDGE_PX) {
                sampleSize *= 2
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sampleSize })
        } finally {
            connection.disconnect()
        }
    }

    /** content:// URI of the saved artwork; the path changes with the image so the car does not show a stale cached one. */
    private fun artworkContentUri(context: Context): Uri? {
        val file = artworkFile(context)
        if (!file.exists()) return null
        return Uri
            .Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(context.packageName + ".carmediaart")
            .appendPath("artwork-${file.lastModified()}.png")
            .build()
    }

    internal fun artworkFileForProvider(context: Context): File = artworkFile(context)

    private fun launchPendingIntent(
        context: Context,
        requestCode: Int,
    ): PendingIntent? {
        val intent =
            context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                ?: return null
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun prefs(context: Context) = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun artworkFile(context: Context) = File(context.applicationContext.filesDir, ARTWORK_FILE_NAME)
}
