package com.urlaunched.android.design.ui.counter.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.counter.models.CounterProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NumberCounterContainer(
    modifier: Modifier = Modifier,
    counterModifier: Modifier = Modifier,
    counterErrorModifier: Modifier = Modifier,
    count: Int,
    step: Int = 1,
    onHoldStep: Int? = null,
    minCount: Int = 1,
    maxCount: Int = Int.MAX_VALUE,
    onValueChange: (step: Int) -> Unit,
    error: String? = null,
    enabled: Boolean = true,
    counterProperties: CounterProperties = CounterProperties(),
    labelContainer: (@Composable () -> Unit)? = null,
    bottomLabelContainer: (@Composable () -> Unit)? = null,
    counterIcon: @Composable () -> Unit,
    counterContainerSize: Dp = Dimens.spacingExtraLarge,
    counterValueContainer: @Composable (value: String) -> Unit,
    errorContainer: (@Composable () -> Unit)? = null
) {
    var isIncreaseButtonHold by remember { mutableStateOf(false) }
    var isReduceButtonHold by remember { mutableStateOf(false) }
    val reduceButtonInteractionSource = remember { MutableInteractionSource() }
    val increaseButtonInteractionSource = remember { MutableInteractionSource() }

    onHoldStep?.let { holdStep ->
        LaunchedEffect(isIncreaseButtonHold, count < maxCount) {
            if (isIncreaseButtonHold) {
                if (count + holdStep < maxCount) {
                    while (isIncreaseButtonHold) {
                        onValueChange(holdStep)
                        delay(counterProperties.incrementStepDelay)
                    }
                } else {
                    onValueChange(maxCount - count)
                }
            }
        }

        LaunchedEffect(isReduceButtonHold, count > minCount) {
            if (isReduceButtonHold) {
                if (count - holdStep > minCount) {
                    while (isReduceButtonHold) {
                        onValueChange(-holdStep)
                        delay(counterProperties.incrementStepDelay)
                    }
                } else {
                    onValueChange(-(count - minCount))
                }
            }
        }
    }

    BaseCounter(
        modifier = modifier,
        counterModifier = counterModifier,
        counterErrorModifier = counterErrorModifier,
        increaseButtonEnabled = count < maxCount,
        reduceButtonEnabled = count > minCount,
        value = count.toString(),
        increaseButtonInteractionSource = increaseButtonInteractionSource,
        reduceButtonInteractionSource = reduceButtonInteractionSource,
        error = error,
        containerEnabled = enabled,
        bottomLabelContainer = bottomLabelContainer,
        labelContainer = labelContainer,
        counterValueContainer = counterValueContainer,
        counterContainerSize = counterContainerSize,
        counterIcon = counterIcon,
        errorContainer = errorContainer
    )

    if (enabled) {
        LaunchedEffect(increaseButtonInteractionSource, count > minCount) {
            increaseButtonInteractionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        if (count > minCount) {
                            onValueChange(-step)
                        }

                        delay(counterProperties.longPressDelay)
                        isReduceButtonHold = true
                    }

                    is PressInteraction.Release -> {
                        isReduceButtonHold = false
                    }
                }
            }
        }

        LaunchedEffect(reduceButtonInteractionSource, count < maxCount) {
            reduceButtonInteractionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        if (count < maxCount) {
                            onValueChange(step)
                        }

                        delay(counterProperties.longPressDelay)

                        isIncreaseButtonHold = true
                    }

                    is PressInteraction.Release -> {
                        isIncreaseButtonHold = false
                    }
                }
            }
        }
    }
}