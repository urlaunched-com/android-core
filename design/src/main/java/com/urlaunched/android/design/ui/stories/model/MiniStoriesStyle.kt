package com.urlaunched.android.design.ui.stories.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.stories.constants.StoryDimens

data class MiniStoriesStyle(
    val maxHeight: Dp = StoryDimens.miniStoriesMaxHeight,
    val hiddenIndicatorBackgroundColor: Color = Color.LightGray,
    val hiddenIndicatorTextStyle: TextStyle = TextStyle(),
    val borderStroke: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    val storiesOffset: DpOffset = DpOffset(x = Dimens.spacingNormal, y = -Dimens.spacingNormal)
)