package io.github.aedev.flow.ui.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
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

    /** The most video tiles a car row takes. 3 matches Nuvio's video-shaped cards; 4 is one number away. */
    const val FEED_COLUMNS = 3

    /** The narrowest a car video tile may be: a narrower window drops a column instead. */
    val FeedCardMinWidth = 200.dp

    private const val TILE_TITLE_LINES = 2
    private val TileDetailsTopPadding = 8.dp
    private val CardInset = 8.dp
    private val PlayPauseButtonSize = 103.dp
    private val PlayPauseIconSize = 103.dp
    private val SkipButtonSize = 84.dp
    private val SkipIconSize = 80.dp
    private val IconButtonSize = 56.dp
    private val IconSize = 32.dp
    private val OverlayButtonSize = IconButtonSize
    private val OverlayIconSize = IconSize
    private val CollapseIconSize = 48.dp
    private val NavIconSize = 32.dp
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

    /** The car's grids use the parent's wide-window margin (24 dp) and row gap instead of its medium ones. */
    val feedUsesLargeSpacing: Boolean get() = CarDisplay.isCar

    /** The car keeps every short last run as tiles; the parent turns it into wide list rows. */
    fun partialRows(parent: Set<Int>): Set<Int> = if (CarDisplay.isCar) emptySet() else parent

    /** A car tile is a thumbnail, a title and one small grey line of views and age: no channel picture, no menu button. */
    val tileShowsExtras: Boolean get() = !CarDisplay.isCar

    fun tileTitleLines(parent: Int): Int = if (CarDisplay.isCar) TILE_TITLE_LINES else parent

    /** The car's detail line is views and upload age only, on one line. */
    fun tileDetailIncludesChannel(parent: Boolean): Boolean = parent && !CarDisplay.isCar

    fun tileDetailLines(parent: Int): Int = if (CarDisplay.isCar) 1 else parent

    /** A step smaller than the parent's tile title, so two lines fit under the thumbnail. */
    @Composable
    fun tileTitleStyle(parent: TextStyle): TextStyle = if (CarDisplay.isCar) MaterialTheme.typography.titleSmall else parent

    /** The card's side inset: two of them are the gap between tiles, so the car's 16 dp gap is 8 dp each. */
    fun cardInset(parent: Dp): Dp = if (CarDisplay.isCar) CardInset else parent

    fun tileDetailsTopPadding(parent: Dp): Dp = if (CarDisplay.isCar) TileDetailsTopPadding else parent

    /** Bottom tab icons, drawn to fill the car's taller bar. */
    fun navIconSize(parent: Dp): Dp = if (CarDisplay.isCar) NavIconSize else parent

    /** The tab label, a typography step larger on the car. */
    @Composable
    fun navLabelStyle(parent: TextStyle): TextStyle = if (CarDisplay.isCar) MaterialTheme.typography.labelLarge else parent

    /** Player buttons: easy to hit while parked. */
    fun playButtonSize(parent: Dp): Dp = if (CarDisplay.isCar) PlayPauseButtonSize else parent

    fun playIconSize(parent: Dp): Dp = if (CarDisplay.isCar) PlayPauseIconSize else parent

    fun skipButtonSize(parent: Dp): Dp = if (CarDisplay.isCar) SkipButtonSize else parent

    fun skipIconSize(parent: Dp): Dp = if (CarDisplay.isCar) SkipIconSize else parent

    fun overlayButtonSize(parent: Dp): Dp = if (CarDisplay.isCar) OverlayButtonSize else parent

    fun overlayIconSize(parent: Dp): Dp = if (CarDisplay.isCar) OverlayIconSize else parent

    /** The player's collapse chevron: a thin glyph that fills little of its box, so its box is drawn larger. */
    fun collapseIconSize(parent: Dp): Dp = if (CarDisplay.isCar) CollapseIconSize else parent

    /** Every icon button outside the player (top bars, back arrows): 56 dp touch with a 32 dp icon, the size Nuvio uses. */
    fun iconButtonModifier(): Modifier = if (CarDisplay.isCar) Modifier.size(IconButtonSize) else Modifier

    fun iconSize(parent: Dp): Dp = if (CarDisplay.isCar) IconSize else parent

    /** The strip under the video that grabs the seek bar. */
    fun seekTouchStrip(parent: Dp): Dp = if (CarDisplay.isCar) SeekTouchStrip else parent

    fun seekTrackRest(parent: Dp): Dp = if (CarDisplay.isCar) SeekTrackRest else parent

    fun seekTrackActive(parent: Dp): Dp = if (CarDisplay.isCar) SeekTrackActive else parent
}
