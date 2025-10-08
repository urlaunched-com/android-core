package com.urlaunched.android.snapshottesting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.LayoutDirection
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import org.junit.Rule
import java.util.Locale
import androidx.compose.ui.unit.LayoutDirection as ComposeLayoutDirection

abstract class BaseSnapshotTest(
    private val deviceConfig: DeviceConfig = DeviceConfig.PIXEL_5,
    renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL,
    private val supportsDarkMode: Boolean = false,
    private val supportsLandscape: Boolean = false,
    private val supportsRtl: Boolean = false,
    private val languages: List<String> = emptyList()
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
        val languages: List<String?> = languages.ifEmpty { listOf(null) }
        val themes = if (supportsDarkMode) listOf(NightMode.NOTNIGHT, NightMode.NIGHT) else listOf(NightMode.NOTNIGHT)

        languages.forEach { lang ->
            val isLangRtl = isRtlLanguage(lang)
            for (theme in themes) {
                updateConfig(theme = theme, landscape = false, languageTagOrIso = lang)
                paparazziSnapshot(
                    name = snapshotName(theme = theme, landscape = false, rtl = false, lang = lang),
                    isRtl = isLangRtl,
                    composable = composable
                )

                if (supportsLandscape) {
                    updateConfig(theme = theme, landscape = true, languageTagOrIso = lang)
                    paparazziSnapshot(
                        name = snapshotName(theme = theme, landscape = true, rtl = false, lang = lang),
                        isRtl = isLangRtl,
                        composable = composable
                    )
                }

                if (!isLangRtl &&
                    supportsRtl &&
                    languages.none { isRtlLanguage(it) } &&
                    lang == languages.first()
                ) {
                    updateConfig(theme = theme, landscape = false, languageTagOrIso = lang, forceRtl = true)
                    paparazziSnapshot(
                        name = snapshotName(theme = theme, landscape = false, rtl = true, lang = lang),
                        isRtl = true,
                        composable = composable
                    )

                    if (supportsLandscape) {
                        updateConfig(theme = theme, landscape = true, languageTagOrIso = lang, forceRtl = true)
                        paparazziSnapshot(
                            name = snapshotName(theme = theme, landscape = true, rtl = true, lang = lang),
                            isRtl = true,
                            composable = composable
                        )
                    }
                }
            }
        }
    }

    private fun snapshotName(theme: NightMode, landscape: Boolean, rtl: Boolean, lang: String?): String {
        val base = buildList {
            add(if (theme == NightMode.NIGHT) "dark" else "light")

            if (landscape) {
                add("landscape")
            }

            if (rtl) {
                add("rtl")
            }
        }.joinToString("_")
        return if (lang.isNullOrBlank()) base else "${base}_$lang"
    }

    private fun isRtlLanguage(languageTagOrIso: String?): Boolean {
        if (languageTagOrIso.isNullOrBlank()) return false
        val primary = languageTagOrIso
            .replace('_', '-')
            .lowercase(Locale.ROOT)
            .substringBefore('-')
        val rtl = setOf(
            "ar", "fa", "he", "iw",
            "ur", "ps", "sd", "ug", "yi", "dv", "ku", "ckb", "nqo"
        )
        return primary in rtl
    }

    private fun String.toLocale(): Locale = if (contains('-') || contains('_')) {
        Locale.forLanguageTag(replace('_', '-'))
    } else {
        Locale(this)
    }

    private fun updateConfig(
        theme: NightMode,
        landscape: Boolean,
        languageTagOrIso: String?,
        forceRtl: Boolean = false
    ) {
        val base = if (landscape) {
            deviceConfig.copy(
                orientation = ScreenOrientation.LANDSCAPE,
                screenHeight = deviceConfig.screenWidth,
                screenWidth = deviceConfig.screenHeight,
                nightMode = theme
            )
        } else {
            deviceConfig.copy(
                orientation = ScreenOrientation.PORTRAIT,
                screenHeight = deviceConfig.screenHeight,
                screenWidth = deviceConfig.screenWidth,
                nightMode = theme
            )
        }
        val cfg = if (!languageTagOrIso.isNullOrBlank()) {
            val desired = languageTagOrIso.toLocale()
            base.copy(
                locale = desired.toLanguageTag(),
                layoutDirection = if (isRtlLanguage(languageTagOrIso) || forceRtl) LayoutDirection.RTL else LayoutDirection.LTR
            )
        } else {
            base.copy(
                layoutDirection = if (forceRtl) LayoutDirection.RTL else LayoutDirection.LTR
            )
        }
        paparazzi.unsafeUpdateConfig(deviceConfig = cfg)
    }

    private fun paparazziSnapshot(name: String, isRtl: Boolean, composable: @Composable () -> Unit) {
        paparazzi.snapshot(name) {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalLayoutDirection provides if (isRtl) ComposeLayoutDirection.Rtl else ComposeLayoutDirection.Ltr
            ) {
                composable()
            }
        }
    }
}