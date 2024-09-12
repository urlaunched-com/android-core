package com.urlaunched.android.design.ui.counter.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun BaseCounter(
    modifier: Modifier,
    counterModifier: Modifier,
    counterErrorModifier: Modifier,
    counterContainerSize: Dp = Dimens.spacingExtraLarge,
    increaseButtonEnabled: Boolean,
    reduceButtonEnabled: Boolean,
    value: String,
    increaseButtonInteractionSource: MutableInteractionSource,
    reduceButtonInteractionSource: MutableInteractionSource,
    containerEnabled: Boolean = true,
    error: String? = null,
    errorContainer: @Composable (() -> Unit)? = null,
    labelContainer: @Composable (() -> Unit)? = null,
    bottomLabelContainer: @Composable (() -> Unit)? = null,
    counterIcon: @Composable () -> Unit,
    counterValueContainer: @Composable (value: String) -> Unit,
) {
    Column(modifier = modifier) {
        labelContainer?.let { label ->
            label()

            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
        }

        Box(
            modifier = counterModifier
                .then(
                    if (error != null) {
                        counterErrorModifier
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacingNormal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CounterButton(
                    interactionSource = increaseButtonInteractionSource,
                    icon = counterIcon,
                    enabled = reduceButtonEnabled && containerEnabled,
                    counterContainerSize = counterContainerSize
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Dimens.spacingSmall),
                    horizontalArrangement = Arrangement.Center
                ) {
                    counterValueContainer(value)
                }

                CounterButton(
                    interactionSource = reduceButtonInteractionSource,
                    icon = counterIcon,
                    enabled = increaseButtonEnabled && containerEnabled,
                    counterContainerSize = counterContainerSize
                )
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))

            errorContainer?.let { errorContainer -> errorContainer() }
        } ?: bottomLabelContainer?.let {
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))

            bottomLabelContainer()
        }
    }
}