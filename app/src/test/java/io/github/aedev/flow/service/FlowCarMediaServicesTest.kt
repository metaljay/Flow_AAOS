package io.github.aedev.flow.service

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], application = Application::class)
class FlowCarMediaServicesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pm get() = context.packageManager
    private val twinA get() = ComponentName(context, FlowCarLaunchableMediaService::class.java)
    private val twinB get() = ComponentName(context, FlowCarLaunchableMediaServiceB::class.java)
    private val old get() = ComponentName(context, FlowCarMediaBrowserService::class.java)

    @Before
    fun registerServices() {
        FlowCarMediaServices.resetForTest()
        listOf(twinA, twinB, old).forEach {
            shadowOf(pm).addOrUpdateService(
                ServiceInfo().apply {
                    packageName = it.packageName
                    name = it.className
                    enabled = it == old
                },
            )
            pm.setComponentEnabledSetting(it, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP)
        }
    }

    private fun carFeature() = shadowOf(pm).setSystemFeature("android.software.car.templates_host.media", true)

    private fun state(component: ComponentName) = pm.getComponentEnabledSetting(component)

    private fun assertOnlyOn(twin: ComponentName) {
        val other = if (twin == twinA) twinB else twinA
        assertThat(state(twin)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        assertThat(state(other)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(state(old)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(FlowCarMediaServices.browseServiceClass(context)).isEqualTo(twin.className)
    }

    @Test
    fun `with the car feature twin A is on first and the plain service off`() {
        carFeature()
        FlowCarMediaServices.apply(context)
        assertOnlyOn(twinA)
    }

    @Test
    fun `without the car feature the plain service stays and both twins are off`() {
        FlowCarMediaServices.apply(context)
        assertThat(state(twinA)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(state(twinB)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(state(old)).isNotEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(FlowCarMediaServices.browseServiceClass(context)).isEqualTo(FlowCarMediaBrowserService::class.java.name)
    }

    @Test
    fun `applying twice is stable`() {
        carFeature()
        FlowCarMediaServices.apply(context)
        FlowCarMediaServices.apply(context)
        assertOnlyOn(twinA)
    }

    @Test
    fun `start-up keeps whichever twin is currently on`() {
        carFeature()
        pm.setComponentEnabledSetting(twinB, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        FlowCarMediaServices.apply(context)
        assertOnlyOn(twinB)
    }

    @Test
    fun `no link since start swaps to the other twin once and the session names it`() {
        carFeature()
        FlowCarMediaServices.apply(context)

        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isTrue()
        assertOnlyOn(twinB)
        FlowCarMediaSession.relinkBeforeFirstActive(context)
        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isFalse()
        assertOnlyOn(twinB)
    }

    @Test
    fun `a second play in the same process does not swap again`() {
        carFeature()
        FlowCarMediaServices.apply(context)
        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isTrue()
        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isFalse()
        assertOnlyOn(twinB)
    }

    @Test
    fun `swapping from twin B goes back to twin A`() {
        carFeature()
        pm.setComponentEnabledSetting(twinB, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        FlowCarMediaServices.apply(context)
        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isTrue()
        assertOnlyOn(twinA)
    }

    @Test
    fun `a linked car means no swap`() {
        carFeature()
        FlowCarMediaServices.apply(context)
        FlowCarMediaServices.markCarLinked()
        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isFalse()
        assertOnlyOn(twinA)
    }

    @Test
    fun `without the car feature there is no swap`() {
        FlowCarMediaServices.apply(context)
        assertThat(FlowCarMediaServices.relinkIfNeeded(context)).isFalse()
        assertThat(state(twinA)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(state(twinB)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
    }

    @Test
    fun `the session extras name the twin that is on after a swap`() {
        carFeature()
        FlowCarMediaServices.apply(context)
        val key = "android.media.session.BROWSE_SERVICE"
        assertThat(FlowCarMediaSession.browseExtras(context).getString(key)).isEqualTo(twinA.className)
        FlowCarMediaSession.get(context)
        FlowCarMediaSession.relinkBeforeFirstActive(context)
        assertOnlyOn(twinB)
        val named = FlowCarMediaSession.browseExtras(context).getString(key)
        assertThat(named).isEqualTo(twinB.className)
    }
}
