package com.urlaunched.android.design.ui.stories

import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.ui.image.UrlImage
import com.urlaunched.android.design.ui.shimmer.shimmer
import com.urlaunched.android.design.ui.stories.constants.StoryConstants
import com.urlaunched.android.design.ui.stories.constants.StoryDimens

@Composable
internal fun <T> MiniStories(
    modifier: Modifier = Modifier,
    stories: List<T>,
    imageModel: (T) -> Any? = { it },
    onStoriesClick: () -> Unit,
    isLoading: Boolean,
    placeholdersCount: Int = StoryConstants.STORY_PLACEHOLDERS_COUNT,
    contentDescription: (T) -> String? = { null },
    storySize: Dp = StoryDimens.miniStoriesMaxHeight,
    visibleImagesCount: Int = StoryConstants.VISIBLE_MINI_STORY_IMAGES_COUNT,
    storyBorder: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    shape: Shape = CircleShape,
    @FloatRange(to = 1.0)
    storyOverlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    hiddenIndicatorBackgroundColor: Color = Color.LightGray,
    hiddenIndicatorTextStyle: TextStyle = TextStyle(),
    maxHiddenCount: Int = StoryConstants.MAX_HIDDEN_MINI_STORIES_COUNT,
    placeholder: @Composable () -> Unit = {},
    errorPlaceholder: @Composable () -> Unit = placeholder,
    loadingPlaceholder: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .shimmer()
        )
    }
) {
    if (isLoading) {
        MiniStoriesShimmer(
            modifier = modifier,
            storySize = storySize,
            visibleImagesCount = visibleImagesCount,
            storyBorder = storyBorder,
            shape = shape,
            storyOverlapFraction = storyOverlapFraction,
            hiddenIndicatorBackgroundColor = hiddenIndicatorBackgroundColor,
            hiddenIndicatorTextStyle = hiddenIndicatorTextStyle,
            maxHiddenCount = maxHiddenCount,
            placeholdersCount = placeholdersCount,
            loadingPlaceholder = loadingPlaceholder
        )
    } else {
        MiniStories(
            modifier = modifier,
            stories = stories,
            visibleImagesCount = visibleImagesCount,
            storyBorder = storyBorder,
            shape = shape,
            storyOverlapFraction = storyOverlapFraction,
            hiddenIndicatorBackgroundColor = hiddenIndicatorBackgroundColor,
            hiddenIndicatorTextStyle = hiddenIndicatorTextStyle,
            maxHiddenCount = maxHiddenCount,
            imageModel = imageModel,
            onStoriesClick = onStoriesClick,
            contentDescription = contentDescription,
            storySize = storySize,
            placeholder = placeholder,
            errorPlaceholder = errorPlaceholder
        )
    }
}

@Composable
private fun MiniStoriesShimmer(
    modifier: Modifier = Modifier,
    placeholdersCount: Int,
    storySize: Dp = StoryDimens.miniStoriesMaxHeight,
    visibleImagesCount: Int = StoryConstants.VISIBLE_MINI_STORY_IMAGES_COUNT,
    storyBorder: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    shape: Shape = CircleShape,
    @FloatRange(to = 1.0)
    storyOverlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    hiddenIndicatorBackgroundColor: Color = Color.LightGray,
    hiddenIndicatorTextStyle: TextStyle = TextStyle(),
    maxHiddenCount: Int = StoryConstants.MAX_HIDDEN_MINI_STORIES_COUNT,
    loadingPlaceholder: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .shimmer()
        )
    }
) {
    MiniStories(
        modifier = modifier,
        stories = List(placeholdersCount) { null },
        onStoriesClick = {
            // Do nothing
        },
        storySize = storySize,
        visibleImagesCount = visibleImagesCount,
        storyBorder = storyBorder,
        shape = shape,
        storyOverlapFraction = storyOverlapFraction,
        hiddenIndicatorBackgroundColor = hiddenIndicatorBackgroundColor,
        hiddenIndicatorTextStyle = hiddenIndicatorTextStyle,
        maxHiddenCount = maxHiddenCount,
        placeholder = loadingPlaceholder
    )
}

@Composable
private fun <T> MiniStories(
    modifier: Modifier = Modifier,
    stories: List<T>,
    imageModel: (T) -> Any? = { it },
    onStoriesClick: () -> Unit,
    contentDescription: (T) -> String? = { null },
    storySize: Dp = StoryDimens.miniStoriesMaxHeight,
    visibleImagesCount: Int = StoryConstants.VISIBLE_MINI_STORY_IMAGES_COUNT,
    storyBorder: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    shape: Shape = CircleShape,
    @FloatRange(to = 1.0)
    storyOverlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    hiddenIndicatorBackgroundColor: Color = Color.LightGray,
    hiddenIndicatorTextStyle: TextStyle = TextStyle(),
    maxHiddenCount: Int = StoryConstants.MAX_HIDDEN_MINI_STORIES_COUNT,
    placeholder: @Composable () -> Unit = {},
    errorPlaceholder: @Composable () -> Unit = placeholder
) {
    val visibleStories = stories.take(visibleImagesCount)
    val hiddenStoriesCount = stories.size - visibleStories.size

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onStoriesClick)
    ) {
        visibleStories.forEachIndexed { index, story ->
            val startPadding = (storySize * (1f - storyOverlapFraction) * index)

            MiniStoryImage(
                model = imageModel(story),
                contentDescription = contentDescription(story),
                border = storyBorder,
                shape = shape,
                placeholder = placeholder,
                errorPlaceholder = errorPlaceholder,
                modifier = Modifier
                    .padding(start = startPadding)
                    .size(storySize)
            )
        }

        if (hiddenStoriesCount > 0) {
            val indicatorIndex = visibleStories.size
            val startPadding = (storySize * (1f - storyOverlapFraction) * indicatorIndex)

            HiddenStoriesIndicator(
                hiddenCount = hiddenStoriesCount,
                maxHiddenCount = maxHiddenCount,
                textStyle = hiddenIndicatorTextStyle,
                backgroundColor = hiddenIndicatorBackgroundColor,
                shape = shape,
                modifier = Modifier
                    .padding(start = startPadding)
                    .size(storySize)
            )
        }
    }
}

@Composable
private fun MiniStoryImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String? = null,
    border: BorderStroke?,
    shape: Shape,
    placeholder: @Composable () -> Unit = {},
    errorPlaceholder: @Composable () -> Unit = placeholder
) {
    UrlImage(
        modifier = modifier
            .aspectRatio(StoryConstants.STORY_IMAGE_ASPECT_RATIO)
            .run {
                if (border != null) this.border(border, shape) else this
            }
            .clip(shape),
        model = model,
        contentDescription = contentDescription,
        scale = ContentScale.Crop,
        placeholder = placeholder,
        errorPlaceholder = errorPlaceholder
    )
}

@Composable
private fun HiddenStoriesIndicator(
    hiddenCount: Int,
    maxHiddenCount: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    backgroundColor: Color,
    shape: Shape
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        val text = if (hiddenCount <= maxHiddenCount) {
            "+$hiddenCount"
        } else {
            "$maxHiddenCount+"
        }
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Preview
@Composable
private fun MiniStoriesPreview() {
    MiniStories(
        stories = List(4) { null },
        onStoriesClick = {}
    )
}

@Preview
@Composable
private fun MiniStoriesShimmerPreview() {
    MiniStoriesShimmer(placeholdersCount = 5)
}