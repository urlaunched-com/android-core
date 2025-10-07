package com.urlaunched.android.design.ui.accountbinding.constants

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

internal object SocialAccountDimens {
    private val defaultSocialAccountCardPadding = 14.dp

    val defaultContentPadding = PaddingValues(
        end = Dimens.spacingNormal,
        start = defaultSocialAccountCardPadding,
        top = defaultSocialAccountCardPadding,
        bottom = defaultSocialAccountCardPadding
    )
}