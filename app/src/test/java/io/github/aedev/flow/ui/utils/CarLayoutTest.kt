package io.github.aedev.flow.ui.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import io.github.aedev.flow.player.BackgroundPlaybackPolicy
import io.github.aedev.flow.ui.components.feedGridLayoutFor
import io.github.aedev.flow.utils.CarDisplay
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
        assertThat(CarLayout.overlayIconSize(24.dp)).isEqualTo(24.dp)
        assertThat(CarLayout.collapseIconSize(32.dp)).isEqualTo(32.dp)
        assertThat(CarLayout.playIconSize(54.dp)).isEqualTo(54.dp)
        assertThat(CarLayout.skipButtonSize(48.dp)).isEqualTo(48.dp)
        assertThat(CarLayout.iconButtonModifier()).isSameInstanceAs(Modifier)
        assertThat(CarLayout.iconSize(24.dp)).isEqualTo(24.dp)
        assertThat(CarLayout.feedUsesLargeSpacing).isFalse()
        assertThat(CarLayout.partialRows(setOf(6, 7))).containsExactly(6, 7)
        assertThat(CarLayout.seekTouchStrip(14.dp)).isEqualTo(14.dp)
        assertThat(CarLayout.tileShowsExtras).isTrue()
    }

    /** [CarDisplay.isCar] has a private setter (a real car sets it from the system), so a test flips its field. */
    private fun setCar(value: Boolean) {
        CarDisplay::class.java
            .getDeclaredField("isCar")
            .apply { isAccessible = true }
            .setBoolean(CarDisplay, value)
    }

    @Test
    fun `on the car the button sizes follow the shared standard`() {
        val wasCar = CarDisplay.isCar
        setCar(true)
        try {
            assertThat(CarLayout.playButtonSize(62.dp)).isEqualTo(103.dp)
            assertThat(CarLayout.playIconSize(54.dp)).isEqualTo(103.dp)
            assertThat(CarLayout.skipButtonSize(48.dp)).isEqualTo(84.dp)
            assertThat(CarLayout.skipIconSize(36.dp)).isEqualTo(80.dp)
            assertThat(CarLayout.overlayButtonSize(40.dp)).isEqualTo(56.dp)
            assertThat(CarLayout.overlayIconSize(24.dp)).isEqualTo(32.dp)
            assertThat(CarLayout.collapseIconSize(32.dp)).isEqualTo(48.dp)
            assertThat(CarLayout.iconButtonModifier()).isNotSameInstanceAs(Modifier)
            assertThat(CarLayout.iconSize(24.dp)).isEqualTo(32.dp)
        } finally {
            setCar(wasCar)
        }
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
