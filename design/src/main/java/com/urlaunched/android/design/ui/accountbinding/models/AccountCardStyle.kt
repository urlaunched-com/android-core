package com.urlaunched.android.design.ui.accountbinding.models

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.accountbinding.constants.SocialAccountDimens
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle

data class AccountCardStyle(
    val containerColor: Color = Color.White,
    val shape: Shape = RoundedCornerShape(Dimens.cornerRadiusBig),
    val shadow: ShadowStyle? = null,
    val contentPadding: PaddingValues = SocialAccountDimens.defaultContentPadding,
    val passwordBasedContentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    val supportingContentPadding: PaddingValues = PaddingValues(top = Dimens.spacingSmall),
    val providerIconSize: Dp = SocialAccountDimens.providerIconSize,
    val providerIconPadding: PaddingValues = PaddingValues(end = Dimens.spacingSmall),
    val trailingIconSpacing: Dp = Dimens.spacingSmall
)