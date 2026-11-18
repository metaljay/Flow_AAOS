package io.github.aedev.flow.player

import io.github.aedev.flow.utils.CarDisplay

object BackgroundPlaybackPolicy {
    fun shouldEnterAutoPip(
        autoPipEnabled: Boolean,
        isVideoPlaying: Boolean,
        explicitBackgroundPlaybackActive: Boolean,
    ): Boolean =
        autoPipEnabled &&
            isVideoPlaying &&
            !explicitBackgroundPlaybackActive

    fun shouldKeepPlaybackInBackground(
        backgroundPlaybackPreferenceEnabled: Boolean,
        explicitBackgroundPlaybackActive: Boolean,
        hasActiveVideo: Boolean,
        // AAOS C12: the car pauses on leaving whatever the Background play setting says; tests pass this in.
        isCar: Boolean = CarDisplay.isCar,
    ): Boolean =
        hasActiveVideo &&
            ((backgroundPlaybackPreferenceEnabled && !isCar) || explicitBackgroundPlaybackActive) // AAOS C12

    fun shouldReopenCurrentVideo(
        requestedVideoId: String,
        currentVideoId: String?,
        isBackgroundPlaybackMode: Boolean,
        isMiniPlayerCollapsed: Boolean,
        hasReusablePlayback: Boolean,
    ): Boolean =
        (isBackgroundPlaybackMode || isMiniPlayerCollapsed) &&
            hasReusablePlayback &&
            requestedVideoId == currentVideoId
}
