package io.github.aedev.flow.ui.utils

import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
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
    fun `the emulator's scaled window fits four tiles a row`() {
        assertThat(carColumns(853)).isEqualTo(4)
    }

    @Test
    fun `a denser car screen drops to three tiles`() {
        assertThat(carColumns(720)).isEqualTo(3)
    }

    @Test
    fun `a very wide window still stops at four`() {
        assertThat(carColumns(1100)).isEqualTo(4)
    }

    @Test
    fun `off the car every helper hands back the parent's value`() {
        assertThat(CarLayout.feedColumns(3)).isEqualTo(3)
        assertThat(CarLayout.feedCardMinWidth(272.dp)).isEqualTo(272.dp)
        assertThat(CarLayout.edgePadding(4.dp)).isEqualTo(4.dp)
        assertThat(CarLayout.tileTitleLines(2)).isEqualTo(2)
        assertThat(CarLayout.tileUploaderLine("Channel", showChannel = true, isUpcoming = false)).isNull()
        assertThat(CarLayout.seekTouchStrip(14.dp)).isEqualTo(14.dp)
        assertThat(CarLayout.tileShowsExtras).isTrue()
    }
}
