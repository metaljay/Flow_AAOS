package io.github.aedev.flow.service

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/** AAOS fork: the launchable twin of [FlowCarMediaBrowserService], switched on only where the car's media app can redirect to Flow. */
class FlowCarLaunchableMediaService : FlowCarMediaBrowserService()

/**
 * Launchable opt-in refills the car's media card after a restart, but without the redirect it makes the
 * app icon open the car's media screen, so exactly one of the two services is enabled, chosen by the car feature.
 */
object FlowCarMediaServices {
    private const val FEATURE_TEMPLATES_HOST_MEDIA = "android.software.car.templates_host.media"

    fun apply(context: Context) {
        val launchable = hasRedirect(context)
        set(
            context,
            FlowCarLaunchableMediaService::class.java,
            if (launchable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        )
        set(
            context,
            FlowCarMediaBrowserService::class.java,
            if (launchable) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
        )
    }

    fun browseServiceClass(context: Context): String =
        (if (hasRedirect(context)) FlowCarLaunchableMediaService::class.java else FlowCarMediaBrowserService::class.java).name

    private fun hasRedirect(context: Context) = context.packageManager.hasSystemFeature(FEATURE_TEMPLATES_HOST_MEDIA)

    private fun set(
        context: Context,
        cls: Class<*>,
        state: Int,
    ) {
        val pm = context.packageManager
        val component = ComponentName(context, cls)
        if (pm.getComponentEnabledSetting(component) != state) {
            pm.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP)
        }
    }
}
