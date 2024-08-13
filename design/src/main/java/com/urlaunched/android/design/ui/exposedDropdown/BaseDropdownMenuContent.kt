package com.urlaunched.android.design.ui.exposedDropdown

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.urlaunched.android.design.ui.exposedDropdown.constants.ExposedDropdownConstants

// A copy of Material3 DropdownMenuContent with the ability to customize shape and background color of popup
@Composable
internal fun BaseDropdownMenuContent(
    modifier: Modifier = Modifier,
    expandedStates: MutableTransitionState<Boolean>,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(expandedStates, ExposedDropdownConstants.DROPDOWN_MENU_TRANSITION_LABEL)

    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = ExposedDropdownConstants.DROPDOWN_MENU_ENTER_EXIT_DURATION,
                    easing = LinearOutSlowInEasing
                )
            } else {
                tween(
                    durationMillis = 1,
                    delayMillis = ExposedDropdownConstants.DROPDOWN_MENU_ENTER_EXIT_DURATION - 1
                )
            }
        }, label = ExposedDropdownConstants.DROPDOWN_MENU_SCALE_ANIMATION_LABEL
    ) { expanded ->
        if (expanded) {
            1f
        } else {
            0.8f
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(durationMillis = ExposedDropdownConstants.DROPDOWN_MENU_FADE_IN_DURATION)
            } else {
                tween(durationMillis = ExposedDropdownConstants.DROPDOWN_MENU_FADE_OUT_DURATION)
            }
        }, label = ExposedDropdownConstants.DROPDOWN_MENU_ALPHA_ANIMATION_LABEL
    ) { expanded ->
        if (expanded) {
            1f
        } else {
            0f
        }
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                transformOrigin = TransformOrigin.Center
            }
    ) {
        content()
    }
}