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
    private val launchable get() = ComponentName(context, FlowCarLaunchableMediaService::class.java)
    private val old get() = ComponentName(context, FlowCarMediaBrowserService::class.java)

    @Before
    fun registerServices() {
        listOf(launchable, old).forEach {
            shadowOf(pm).addOrUpdateService(
                ServiceInfo().apply {
                    packageName = it.packageName
                    name = it.className
                    enabled = it == old
                },
            )
        }
    }

    @Test
    fun `with the car feature the launchable service is on and the old one off`() {
        shadowOf(pm).setSystemFeature("android.software.car.templates_host.media", true)
        FlowCarMediaServices.apply(context)
        assertThat(pm.getComponentEnabledSetting(launchable)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        assertThat(pm.getComponentEnabledSetting(old)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(FlowCarMediaServices.browseServiceClass(context)).isEqualTo(FlowCarLaunchableMediaService::class.java.name)
    }

    @Test
    fun `without the car feature the old service stays and the launchable one is off`() {
        FlowCarMediaServices.apply(context)
        assertThat(pm.getComponentEnabledSetting(launchable)).isNotEqualTo(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        assertThat(pm.getComponentEnabledSetting(old)).isNotEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        assertThat(FlowCarMediaServices.browseServiceClass(context)).isEqualTo(FlowCarMediaBrowserService::class.java.name)
    }

    @Test
    fun `applying twice is stable`() {
        shadowOf(pm).setSystemFeature("android.software.car.templates_host.media", true)
        FlowCarMediaServices.apply(context)
        FlowCarMediaServices.apply(context)
        assertThat(pm.getComponentEnabledSetting(launchable)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        assertThat(pm.getComponentEnabledSetting(old)).isEqualTo(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
    }
}
