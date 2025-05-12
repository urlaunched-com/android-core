package com.urlaunched.android.snapshottesting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import org.junit.Rule

abstract class BaseSnapshotTest(
    private val deviceConfig: DeviceConfig = DeviceConfig.PIXEL_5,
    renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL,
    private val supportsDarkMode: Boolean = false,
    private val supportsLandscape: Boolean = false,
    private val supportsRtl: Boolean = false
) {
    @get:Rule
    val localRule = DefaultLocaleRule()

    @get:Rule
    open val paparazzi =
        Paparazzi(
            deviceConfig = deviceConfig,
            renderingMode = renderingMode,
            showSystemUi = false,
            maxPercentDifference = 0.1,
            environment = detectEnvironment().copy(
                compileSdkVersion = 34
            )
        )

    fun snapshot(composable: @Composable () -> Unit) {
        when {
            supportsDarkMode && supportsLandscape && supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)

                lightLandscapeConfig()
                paparazziSnapshot("light_landscape", false, composable)

                lightPortraitConfig()
                paparazziSnapshot("light_rtl", true, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", false, composable)

                darkLandscapeConfig()
                paparazziSnapshot("dark_landscape", false, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark_rtl", true, composable)
            }

            supportsDarkMode && supportsLandscape && !supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)

                lightLandscapeConfig()
                paparazziSnapshot("light_landscape", false, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", false, composable)

                darkLandscapeConfig()
                paparazziSnapshot("dark_landscape", false, composable)
            }

            supportsDarkMode && !supportsLandscape && supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)

                paparazziSnapshot("light_rtl", true, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", false, composable)

                paparazziSnapshot("dark_rtl", true, composable)
            }

            supportsDarkMode && !supportsLandscape && !supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", false, composable)
            }

            !supportsDarkMode && supportsLandscape && !supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)

                lightLandscapeConfig()
                paparazziSnapshot("light_landscape", false, composable)
            }

            !supportsDarkMode && !supportsLandscape && supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)

                paparazziSnapshot("light_rtl", true, composable)
            }

            else -> {
                lightPortraitConfig()
                paparazziSnapshot("light", false, composable)
            }
        }
    }

    private fun paparazziSnapshot(name: String, isRtl: Boolean, composable: @Composable () -> Unit) {
        paparazzi.snapshot(name) {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                composable()
            }
        }
    }

    private val lightPortraitConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                nightMode = NightMode.NOTNIGHT
            )
        )
    }

    private val lightLandscapeConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                orientation = ScreenOrientation.LANDSCAPE,
                screenHeight = deviceConfig.screenWidth,
                screenWidth = deviceConfig.screenHeight,
                nightMode = NightMode.NOTNIGHT
            )
        )
    }

    private val darkPortraitConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                nightMode = NightMode.NIGHT
            )
        )
    }

    private val darkLandscapeConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                orientation = ScreenOrientation.LANDSCAPE,
                screenHeight = deviceConfig.screenWidth,
                screenWidth = deviceConfig.screenHeight,
                nightMode = NightMode.NIGHT
            )
        )
    }
}