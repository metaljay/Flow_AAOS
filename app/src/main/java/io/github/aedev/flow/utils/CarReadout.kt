package io.github.aedev.flow.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import io.github.aedev.flow.R

private const val FEATURE_TEMPLATES_HOST = "android.software.car.templates_host"
private const val FEATURE_TEMPLATES_HOST_MEDIA = "android.software.car.templates_host.media"
private const val ACTION_MEDIA_TEMPLATE = "android.car.intent.action.MEDIA_TEMPLATE"

/**
 * AAOS fork (C8): what this car's software reports about the home screen media card, shown as the
 * first group of About > Device Info so it can be read off the car's screen. Nobody can read logs
 * off the car, so this is the only way to learn it. Null off the car.
 */
object CarReadout {
    fun group(context: Context): DeviceFactGroup? {
        if (!CarDisplay.isCar) return null
        val packages = context.packageManager
        val yes = context.getString(R.string.device_info_supported)
        val no = context.getString(R.string.device_info_not_supported)

        fun feature(name: String) = DeviceFact(name.removePrefix("android.software.car."), if (packages.hasSystemFeature(name)) yes else no)
        return DeviceFactGroup(
            R.string.aaos_car_readout_section,
            listOf(
                feature(FEATURE_TEMPLATES_HOST_MEDIA),
                feature(FEATURE_TEMPLATES_HOST),
                DeviceFact(
                    context.getString(R.string.aaos_car_readout_home),
                    handler(context, Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)),
                ),
                DeviceFact(context.getString(R.string.aaos_car_readout_media), handler(context, Intent(ACTION_MEDIA_TEMPLATE))),
            ),
        )
    }

    private fun handler(
        context: Context,
        intent: Intent,
    ): String {
        val packages = context.packageManager
        val name =
            runCatching { packages.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName }.getOrNull()
                ?: return context.getString(R.string.aaos_car_readout_hidden)
        val version = runCatching { packages.getPackageInfo(name, 0).versionName }.getOrNull() ?: return name
        return context.getString(R.string.aaos_car_readout_package_value, name, version)
    }
}
