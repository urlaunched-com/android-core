package com.urlaunched.android.design.ui.stories.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.stories.constants.StoryDimens

data class StoriesStyle(
    val minHeight: Dp = StoryDimens.miniStoriesMaxHeight,
    val maxHeight: Dp = StoryDimens.maxHeight,
    val arrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    val storyBackgroundColor: Color = Color.White,
    val borderStroke: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    val storyBorderPadding: PaddingValues = PaddingValues(StoryDimens.borderPadding),
    val storiesRowPadding: PaddingValues =
        PaddingValues(horizontal = Dimens.spacingNormal, vertical = Dimens.spacingNormal)
)