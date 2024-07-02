package com.urlaunched.android.snapshottesting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.NightMode
import com.urlaunched.android.design.ui.paging.LocalPagingMode
import com.urlaunched.android.design.ui.paging.LocalPagingModeEnum
import org.junit.Rule
import java.util.Locale

abstract class BaseSnapshotTest(
    renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL,
    private val supportsDarkMode: Boolean = false
) {
    init {
        Locale.setDefault(Locale.ENGLISH)
    }

    @get:Rule
    open val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_5,
            renderingMode = renderingMode,
            showSystemUi = false,
            maxPercentDifference = 0.1,
            environment = detectEnvironment().run {
                copy(compileSdkVersion = 33, platformDir = platformDir.replace("34", "33"))
            }
        )

    fun snapshot(pagingMode: LocalPagingModeEnum? = null, composable: @Composable () -> Unit) {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = DeviceConfig.PIXEL_5.copy(
                nightMode = NightMode.NOTNIGHT
            )
        )

        paparazzi.snapshot("light") {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalPagingMode provides pagingMode
            ) {
                composable()
            }
        }

        if (supportsDarkMode) {
            paparazzi.unsafeUpdateConfig(
                deviceConfig = DeviceConfig.PIXEL_5.copy(
                    nightMode = NightMode.NIGHT
                )
            )

            paparazzi.snapshot("dark") {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalPagingMode provides pagingMode
                ) {
                    composable()
                }
            }
        }
    }
}