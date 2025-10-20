package com.urlaunched.android.design.ui.stories.model

import androidx.annotation.FloatRange
import com.urlaunched.android.design.ui.stories.constants.StoryConstants

data class MiniStoriesConfig(
    @FloatRange(to = 1.0)
    val overlapFraction: Float = StoryConstants.MINI_STORIES_OVERLAP_FRACTION,
    val placeholdersCount: Int = StoryConstants.STORY_PLACEHOLDERS_COUNT,
    val maxHiddenCount: Int = StoryConstants.MAX_HIDDEN_MINI_STORIES_COUNT,
    val visibleImagesCount: Int = StoryConstants.VISIBLE_MINI_STORY_IMAGES_COUNT
)