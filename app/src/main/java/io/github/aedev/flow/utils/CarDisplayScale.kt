package io.github.aedev.flow.utils

import android.content.Context
import android.content.res.Configuration
import kotlin.math.roundToInt

private const val CAR_DISPLAY_SCALE = 1.5f

/**
 * AAOS fork: scales every dp and sp in one place, so buttons, icons and text suit a car touchscreen
 * and new parent screens grow with no extra edits. It is a delta, so the theme, locale and font
 * scale still follow the system.
 */
fun carDisplayConfiguration(base: Context): Configuration =
    Configuration().apply {
        densityDpi = (base.resources.configuration.densityDpi * CAR_DISPLAY_SCALE).roundToInt()
    }
