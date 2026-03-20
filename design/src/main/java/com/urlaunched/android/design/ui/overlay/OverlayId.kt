package com.urlaunched.android.design.ui.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

private enum class OverlayId {
    CONTENT
}

/**
 * To use this correctly:
 *
 * 1. Track the position and size of the component you want to attach the overlay to using `Modifier.onGloballyPositioned { ... }`
 * 2. From that callback, capture `positionInWindow()` and `size` into a `OverlayPositionInfo`
 * 3. Pass that info to [CustomOverlay] when triggering it (e.g. on long press or tap)
 *
 * Example:
 *
 * ```kotlin
 * var overlayInfo: OverlayPositionInfo? by remember { mutableStateOf(null) }
 * var isOverlayVisible by remember { mutableStateOf(false) }
 *
 * Box(modifier = Modifier.onGloballyPositioned { coords ->
 *     overlayInfo = OverlayPositionInfo(
 *         position = coords.positionInWindow(),
 *         size = coords.size
 *     )
 * }) {
 *     // item
 * }
 *
 * if (isOverlayVisible && overlayInfo != null) {
 *     CustomOverlay(
 *         positionInfo = overlayInfo!!,
 *         config = OverlayConfig(...),
 *         onDismissRequest = { isOverlayVisible = false }
 *     ) {
 *         // your overlay content here
 *     }
 * }
 * ```
 *
 * @param positionInfo The position and size of the anchor element to align overlay content
 * @param config OverlayConfig with customization options
 * @param onDismissRequest Called when overlay should be dismissed (e.g. outside click)
 * @param content The content to display in the overlay
 */

@Composable
fun CustomOverlay(
    positionInfo: OverlayPositionInfo,
    config: OverlayConfig = OverlayConfig(),
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = config.usePlatformDefaultWidth,
            dismissOnClickOutside = config.dismissOnClickOutside,
            dismissOnBackPress = config.dismissOnBackPress,
            decorFitsSystemWindows = true,
        )
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            dialogWindow?.let { window ->
                window.setDimAmount(config.backgroundAlpha)
                config.windowAnimationsRes?.let { window.setWindowAnimations(it) }
            }
        }

        Layout(
            modifier = Modifier.clickable(
                onClick = onDismissRequest,
                indication = null,
                interactionSource = null
            ),
            content = {
                Box(modifier = Modifier.layoutId(OverlayId.CONTENT)) {
                    content()
                }
            }
        ) { measurables, constraints ->

            val contentMeasurable = measurables.first { it.layoutId == OverlayId.CONTENT }

            val placeable = if (config.widthOverride != null) {
                contentMeasurable.measure(Constraints.fixedWidth(config.widthOverride))
            } else {
                contentMeasurable.measure(
                    Constraints(
                        maxWidth = constraints.maxWidth,
                        maxHeight = constraints.maxHeight
                    )
                )
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                val x = positionInfo.position.x.toInt().coerceIn(0, constraints.maxWidth - placeable.width)
                val y = (positionInfo.position.y.toInt() - statusBarHeight).coerceAtLeast(0)
                placeable.place(x, y)
            }
        }
    }
}