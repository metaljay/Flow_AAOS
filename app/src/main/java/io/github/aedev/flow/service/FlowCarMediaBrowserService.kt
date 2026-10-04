package io.github.aedev.flow.service

import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService

/**
 * AAOS fork: the media source the car's home screen card follows (opted in with
 * `androidx.car.app.launchable` in the manifest). It hands out [FlowCarMediaSession], which mirrors
 * whichever of Flow's video or music players played last, and refills it from disk when the car
 * binds after the app was closed.
 */
class FlowCarMediaBrowserService : MediaBrowserService() {
    override fun onCreate() {
        super.onCreate()
        FlowCarMediaSession.startObserving(this)
        FlowCarMediaSession.restoreIfEmpty(this)
        sessionToken = FlowCarMediaSession.get(this).sessionToken
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot {
        val wantsRecent = rootHints?.getBoolean(BrowserRoot.EXTRA_RECENT) == true
        val extras = if (wantsRecent) Bundle().apply { putBoolean(BrowserRoot.EXTRA_RECENT, true) } else null
        return BrowserRoot(if (wantsRecent) RECENT_ROOT_ID else ROOT_ID, extras)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>,
    ) {
        val items =
            when (parentId) {
                ROOT_ID -> listOf(FlowCarMediaSession.continueFolder(this))
                RECENT_ROOT_ID, FlowCarMediaSession.CONTINUE_ID -> listOfNotNull(FlowCarMediaSession.lastPlayedBrowseItem(this))
                else -> emptyList()
            }
        result.sendResult(items.toMutableList())
    }

    private companion object {
        const val ROOT_ID = "flow_root"
        const val RECENT_ROOT_ID = "flow_recent"
    }
}
