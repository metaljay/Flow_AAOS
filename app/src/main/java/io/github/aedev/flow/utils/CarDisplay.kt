package io.github.aedev.flow.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import kotlin.math.roundToInt

private const val CAR_DISPLAY_SCALE = 1.5f

/**
 * AAOS fork: whether the app is running on a car screen, and the one size scale it gets there.
 * Parent files read [isCar] through one-line hooks tagged `// AAOS Cx`; it stays false in unit
 * tests and on phones, so the parent's own behaviour and tests are untouched.
 */
object CarDisplay {
    @Volatile
    var isCar: Boolean = false
        private set

    /**
     * Scales every dp and sp in one place, so buttons, icons and text suit a car touchscreen and new
     * parent screens grow with no extra edits. It is a delta, so the theme, locale and font scale
     * still follow the system.
     */
    fun configuration(base: Context): Configuration {
        isCar = base.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
        return Configuration().apply {
            if (isCar) densityDpi = (base.resources.configuration.densityDpi * CAR_DISPLAY_SCALE).roundToInt()
        }
    }
}
