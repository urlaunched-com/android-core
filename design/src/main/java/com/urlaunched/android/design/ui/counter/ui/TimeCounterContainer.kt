package com.urlaunched.android.design.ui.counter.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import kotlinx.coroutines.flow.collectLatest


@Composable
fun TimeCounterContainer(
    modifier: Modifier = Modifier,
    counterModifier: Modifier = Modifier,
    counterErrorModifier: Modifier = Modifier,
    increaseButtonEnabled: Boolean = true,
    reduceButtonEnabled: Boolean = true,
    timeValue: String,
    onTimeChange: (timeChange: Int) -> Unit,
    enabled: Boolean = true,
    timeChangeMinutes: Int,
    error: String? = null,
    labelContainer: (@Composable () -> Unit)? = null,
    bottomLabelContainer: (@Composable () -> Unit)? = null,
    counterIcon: @Composable () -> Unit,
    counterContainerSize: Dp = Dimens.spacingExtraLarge,
    counterValueContainer: @Composable (value: String) -> Unit,
    errorContainer: (@Composable () -> Unit)? = null
) {
    val reduceButtonInteractionSource = remember { MutableInteractionSource() }
    val increaseButtonInteractionSource = remember { MutableInteractionSource() }

    BaseCounter(
        modifier = modifier,
        counterModifier = counterModifier,
        counterErrorModifier = counterErrorModifier,
        increaseButtonEnabled = increaseButtonEnabled,
        reduceButtonEnabled = reduceButtonEnabled,
        value = timeValue,
        increaseButtonInteractionSource = reduceButtonInteractionSource,
        reduceButtonInteractionSource = increaseButtonInteractionSource,
        containerEnabled = enabled,
        error = error,
        bottomLabelContainer = bottomLabelContainer,
        labelContainer = labelContainer,
        counterValueContainer = counterValueContainer,
        counterContainerSize = counterContainerSize,
        counterIcon = counterIcon,
        errorContainer = errorContainer
    )

    if (increaseButtonEnabled && enabled) {
        LaunchedEffect(increaseButtonInteractionSource) {
            increaseButtonInteractionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        onTimeChange(timeChangeMinutes)
                    }
                }
            }
        }
    }

    if (reduceButtonEnabled && enabled) {
        LaunchedEffect(reduceButtonInteractionSource) {
            reduceButtonInteractionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        onTimeChange(-timeChangeMinutes)
                    }
                }
            }
        }
    }
}