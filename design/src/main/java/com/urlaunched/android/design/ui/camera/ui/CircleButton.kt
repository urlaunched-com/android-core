package com.urlaunched.android.design.ui.camera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.LightGray,
    iconSize: Dp = Dimens.iconSizeNormal,
    innerPadding: Dp = Dimens.spacingNormalSpecial,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(innerPadding)
    ) {
        Box(modifier = Modifier.size(iconSize), propagateMinConstraints = true) {
            icon()
        }
    }
}