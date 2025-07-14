package com.urlaunched.android.design.ui.question.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

data class QuestionDimens(
    val borderWidth: Dp = 1.dp,
    val borderShape: Dp = 14.dp,
    val horizontalPadding: Dp = Dimens.spacingNormalSpecial,
    val minHeight: Dp = 64.dp,
    val answerTextVerticalPadding: Dp = Dimens.spacingNormalSpecial,
    val questionButtonSpacing: Dp = Dimens.spacingNormalSpecial,
)