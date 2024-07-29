package com.urlaunched.android.design.ui.textfield.models

import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens

data class TextFieldsSpacerConfig(
    val labelSpacer: Dp = Dimens.spacingSmallSpecial,
    val labelIconSpacer: Dp = Dimens.spacingSmall,
    val leadingIconSpacer: Dp = Dimens.spacingSmall,
    val trailingIconSpacer: Dp = Dimens.spacingSmall,
    val errorSpacer: Dp = Dimens.spacingTinyHalf
)