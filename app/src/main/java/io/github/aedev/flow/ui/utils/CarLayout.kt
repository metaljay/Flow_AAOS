package io.github.aedev.flow.ui.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.aedev.flow.ui.components.FeedGridLayout
import io.github.aedev.flow.utils.CarDisplay

/**
 * AAOS fork: every car-only look decision, in one place. Parent files reach these through one-line
 * hooks tagged `// AAOS Cx`, and each helper hands back the parent's own value off the car. The dp
 * values here are before the whole-app scale in [CarDisplay], so they draw 1.5 times larger.
 */
object CarLayout {
    /**
     * The car's rounded corners and bezel cover the window edge. Added to a bar's own 4 dp padding
     * this puts its first touch target 16 dp in, on the same line as the feed's thumbnails.
     */
    val EdgeInset = 12.dp

    /** The most video tiles a car row takes. */
    const val FEED_COLUMNS = 4

    /** The narrowest a car video tile may be: a narrower window drops a column instead. */
    val FeedCardMinWidth = 200.dp

    private const val TILE_TITLE_LINES = 1
    private val SeekTouchStrip = 24.dp
    private val SeekTrackRest = 7.dp
    private val SeekTrackActive = 12.dp

    /** The parent's insets, plus [EdgeInset] at the left and right on the car. */
    fun edgeInsets(parent: WindowInsets): WindowInsets =
        if (CarDisplay.isCar) parent.add(WindowInsets(left = EdgeInset, right = EdgeInset)) else parent

    /** The parent's horizontal padding, plus [EdgeInset] on the car. */
    fun edgePadding(parent: Dp): Dp = if (CarDisplay.isCar) parent + EdgeInset else parent

    fun feedColumns(parent: Int): Int = if (CarDisplay.isCar) FEED_COLUMNS else parent

    fun feedCardMinWidth(parent: Dp): Dp = if (CarDisplay.isCar) FeedCardMinWidth else parent

    /** Continue Watching tiles match the feed's tiles on the car, so Home reads as one grid. */
    fun stripCardWidth(
        feed: FeedGridLayout,
        parent: Dp,
    ): Dp = if (CarDisplay.isCar && !feed.isCompact) feed.cardWidth else parent

    /** A car tile is a thumbnail, a one-line title and the uploader: no channel picture, no menu button. */
    val tileShowsExtras: Boolean get() = !CarDisplay.isCar

    fun tileTitleLines(parent: Int): Int = if (CarDisplay.isCar) TILE_TITLE_LINES else parent

    /** The uploader alone when the car tile has one to show; null keeps the parent's detail line. */
    fun tileUploaderLine(
        channelName: String,
        showChannel: Boolean,
        isUpcoming: Boolean,
    ): String? = channelName.takeIf { CarDisplay.isCar && showChannel && !isUpcoming }

    /** The strip under the video that grabs the seek bar. */
    fun seekTouchStrip(parent: Dp): Dp = if (CarDisplay.isCar) SeekTouchStrip else parent

    fun seekTrackRest(parent: Dp): Dp = if (CarDisplay.isCar) SeekTrackRest else parent

    fun seekTrackActive(parent: Dp): Dp = if (CarDisplay.isCar) SeekTrackActive else parent
}
