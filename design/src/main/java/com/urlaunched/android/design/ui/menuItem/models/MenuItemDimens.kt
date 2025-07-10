package com.urlaunched.android.design.ui.menuItem.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

data class MenuItemDimens(
    val containerHeight: Dp = 48.dp,
    val cornerRadiusShape: Dp = Dimens.cornerRadiusNormalSpecial,
    val startPadding: Dp = Dimens.spacingBigSpecial,
    val endPadding: Dp = Dimens.spacingNormal,
    val topPadding: Dp = Dimens.spacingNormalSpecial,
    val bottomPadding: Dp = Dimens.spacingNormalSpecial,
    val spacerHeight: Dp = Dimens.spacingSmall
)