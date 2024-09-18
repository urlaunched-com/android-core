package com.urlaunched.android.design.ui.counter.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun CounterButton(
    icon: @Composable () -> Unit = {},
    counterContainerSize: Dp,
    interactionSource: MutableInteractionSource,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(counterContainerSize)
            .clickable(
                interactionSource = interactionSource,
                indication = if (enabled) LocalIndication.current else null,
                onClick = {
                    // Do nothing. Handle clicks in interaction source
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
@Preview
private fun CounterButtonPreview() {
    CounterButton(
        icon = {},
        counterContainerSize = Dimens.spacingBig,
        interactionSource = remember { MutableInteractionSource() },
        enabled = true
    )
}