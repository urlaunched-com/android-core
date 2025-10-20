package com.urlaunched.android.design.ui.stories.model

import androidx.annotation.FloatRange
import com.urlaunched.android.design.ui.stories.constants.StoryConstants

data class StoriesConfig(
    @FloatRange(to = 1.0)
    val overlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    val placeholdersCount: Int = StoryConstants.STORY_PLACEHOLDERS_COUNT,
    val collapsingCoefficient: Int = StoryConstants.STORIES_COLLAPSING_COEFFICIENT
)