package io.github.aedev.flow.service

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/** AAOS fork: the launchable twin of [FlowCarMediaBrowserService], switched on only where the car's media app can redirect to Flow. */
class FlowCarLaunchableMediaService : FlowCarMediaBrowserService()

/**
 * A second, identical launchable twin. The car only rebuilds its link to Flow's media service (a force-stop or
 * update kills it) when the service the app names changes, so [FlowCarMediaServices.relinkIfNeeded] swaps between
 * the two twins at the first play when the car has not linked since the process started.
 */
class FlowCarLaunchableMediaServiceB : FlowCarMediaBrowserService()

/**
 * Launchable opt-in refills the car's media card after a restart, but without the redirect it makes the
 * app icon open the car's media screen, so on a car with the feature exactly one launchable twin is enabled
 * and the plain service is off; elsewhere both twins are off.
 */
object FlowCarMediaServices {
    private const val FEATURE_TEMPLATES_HOST_MEDIA = "android.software.car.templates_host.media"

    private val twinA = FlowCarLaunchableMediaService::class.java
    private val twinB = FlowCarLaunchableMediaServiceB::class.java

    /** Set when a client other than Flow binds the browse service: the car's link to this process is alive. */
    @Volatile
    private var carLinked = false

    private var relinkChecked = false

    /** Start-up: keeps whichever twin is enabled (twin A on a first install) and never swaps, as the car may be binding now. */
    fun apply(context: Context) {
        if (hasRedirect(context)) {
            val current = enabledTwin(context)
            set(context, current, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            set(context, other(current), PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            set(context, FlowCarMediaBrowserService::class.java, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        } else {
            set(context, twinA, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            set(context, twinB, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            set(context, FlowCarMediaBrowserService::class.java, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        }
    }

    fun browseServiceClass(context: Context): String =
        (if (hasRedirect(context)) enabledTwin(context) else FlowCarMediaBrowserService::class.java).name

    internal fun markCarLinked() {
        carLinked = true
    }

    /**
     * Called once, just before this process first publishes an active (playing or buffering) state. If the car
     * has not bound the browse service since the process started, its link may be dead (force-stop, update), so
     * this switches on the other twin and then switches off the old one. Returns true when it swapped, and the
     * caller must then name [browseServiceClass] in the session's extras before publishing.
     */
    fun relinkIfNeeded(context: Context): Boolean {
        if (relinkChecked) return false
        relinkChecked = true
        if (carLinked || !hasRedirect(context)) return false
        val old = enabledTwin(context)
        set(context, other(old), PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        set(context, old, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        return true
    }

    /** Test hook: forget the car's link and the once-per-process check. */
    internal fun resetForTest() {
        carLinked = false
        relinkChecked = false
    }

    private fun other(twin: Class<*>): Class<*> = if (twin == twinA) twinB else twinA

    /** The twin that is switched on (both are off in the manifest); twin A when neither is. */
    private fun enabledTwin(context: Context): Class<*> = listOf(twinA, twinB).firstOrNull { isEnabled(context, it) } ?: twinA

    private fun isEnabled(
        context: Context,
        cls: Class<*>,
    ) = context.packageManager.getComponentEnabledSetting(ComponentName(context, cls)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED

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
