package io.github.aedev.flow.ui.utils

import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import io.github.aedev.flow.player.BackgroundPlaybackPolicy
import io.github.aedev.flow.ui.components.feedGridLayoutFor
import org.junit.Test

/** AAOS fork: the car's tile sizing, one minimum width deciding how many fit a row. */
class CarLayoutTest {
    private fun carColumns(windowWidth: Int): Int =
        feedGridLayoutFor(
            maxWidth = windowWidth.dp,
            maxAutoColumns = CarLayout.FEED_COLUMNS,
            minCellWidth = CarLayout.FeedCardMinWidth,
        ).columns

    @Test
    fun `the emulator's scaled window fits the car's tile count`() {
        assertThat(carColumns(853)).isEqualTo(CarLayout.FEED_COLUMNS)
    }

    @Test
    fun `a narrow window drops a column instead of shrinking tiles`() {
        assertThat(carColumns(600)).isEqualTo(2)
    }

    @Test
    fun `a very wide window still stops at the car's tile count`() {
        assertThat(carColumns(1100)).isEqualTo(CarLayout.FEED_COLUMNS)
    }

    @Test
    fun `off the car every helper hands back the parent's value`() {
        assertThat(CarLayout.feedColumns(3)).isEqualTo(3)
        assertThat(CarLayout.feedCardMinWidth(272.dp)).isEqualTo(272.dp)
        assertThat(CarLayout.edgePadding(4.dp)).isEqualTo(4.dp)
        assertThat(CarLayout.tileTitleLines(2)).isEqualTo(2)
        assertThat(CarLayout.tileDetailsTopPadding(12.dp)).isEqualTo(12.dp)
        assertThat(CarLayout.cardInset(12.dp)).isEqualTo(12.dp)
        assertThat(CarLayout.navIconSize(24.dp)).isEqualTo(24.dp)
        assertThat(CarLayout.playButtonSize(62.dp)).isEqualTo(62.dp)
        assertThat(CarLayout.skipIconSize(36.dp)).isEqualTo(36.dp)
        assertThat(CarLayout.overlayButtonSize(40.dp)).isEqualTo(40.dp)
        assertThat(CarLayout.feedUsesLargeSpacing).isFalse()
        assertThat(CarLayout.partialRows(setOf(6, 7))).containsExactly(6, 7)
        assertThat(CarLayout.seekTouchStrip(14.dp)).isEqualTo(14.dp)
        assertThat(CarLayout.tileShowsExtras).isTrue()
    }

    private fun keepsPlaying(
        setting: Boolean,
        explicit: Boolean,
        isCar: Boolean,
    ) = BackgroundPlaybackPolicy.shouldKeepPlaybackInBackground(
        backgroundPlaybackPreferenceEnabled = setting,
        explicitBackgroundPlaybackActive = explicit,
        hasActiveVideo = true,
        isCar = isCar,
    )

    @Test
    fun `on the car the background play setting is ignored`() {
        assertThat(keepsPlaying(setting = true, explicit = false, isCar = true)).isFalse()
    }

    @Test
    fun `on the car an explicit play in background request still keeps playing`() {
        assertThat(keepsPlaying(setting = true, explicit = true, isCar = true)).isTrue()
        assertThat(keepsPlaying(setting = false, explicit = true, isCar = true)).isTrue()
    }

    @Test
    fun `off the car the background play setting still keeps playing`() {
        assertThat(keepsPlaying(setting = true, explicit = false, isCar = false)).isTrue()
    }
}
