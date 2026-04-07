package com.urlaunched.android.design.ui.stories

import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.shimmer.shimmer
import com.urlaunched.android.design.ui.stories.constants.StoryConstants
import com.urlaunched.android.design.ui.stories.constants.StoryDimens
import com.urlaunched.android.design.ui.topbar.CollapsingTopBarState
import com.urlaunched.android.design.ui.topbar.rememberCollapsingTopBarState
import kotlin.math.roundToInt

@Composable
internal fun <T> CollapsingStoriesList(
    modifier: Modifier = Modifier,
    stories: List<T>,
    isLoading: Boolean,
    imageModel: (T) -> Any?,
    contentDescription: (T) -> String? = { null },
    onStoryClick: (story: T) -> Unit,
    placeholdersCount: Int = StoryConstants.STORY_PLACEHOLDERS_COUNT,
    lazyListState: LazyListState = rememberLazyListState(),
    topBarState: CollapsingTopBarState,
    contentPadding: PaddingValues = PaddingValues(Dimens.zeroDp),
    @FloatRange(to = 1.0)
    storiesOverlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    storiesArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    storyShape: Shape = CircleShape,
    storyBackgroundColor: Color = Color.Transparent,
    storyBorder: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    storyBorderPadding: PaddingValues = PaddingValues(StoryDimens.borderPadding),
    miniStoryMaxHeight: Dp = StoryDimens.miniStoriesMaxHeight,
    storyMaxHeight: Dp = StoryDimens.maxHeight,
    storiesCollapsingCoefficient: Int = StoryConstants.STORIES_COLLAPSING_COEFFICIENT,
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
        CollapsableLazyStoriesShimmerRow(
            modifier = modifier,
            placeholdersCount = placeholdersCount,
            topBarState = topBarState,
            lazyListState = lazyListState,
            contentPadding = contentPadding,
            storiesOverlapFraction = storiesOverlapFraction,
            storiesArrangement = storiesArrangement,
            storyBackgroundColor = storyBackgroundColor,
            storyShape = storyShape,
            storyBorder = storyBorder,
            storyBorderPadding = storyBorderPadding,
            miniStoryMaxHeight = miniStoryMaxHeight,
            storyMaxHeight = storyMaxHeight,
            storiesCollapsingCoefficient = storiesCollapsingCoefficient,
            loadingPlaceholder = loadingPlaceholder
        )
    } else {
        CollapsableLazyStoriesRow(
            modifier = modifier,
            topBarState = topBarState,
            lazyListState = lazyListState,
            contentPadding = contentPadding,
            storiesOverlapFraction = storiesOverlapFraction,
            storiesArrangement = storiesArrangement,
            storyShape = storyShape,
            storyBorder = storyBorder,
            storyBackgroundColor = storyBackgroundColor,
            storyBorderPadding = storyBorderPadding,
            miniStoryMaxHeight = miniStoryMaxHeight,
            storyMaxHeight = storyMaxHeight,
            storiesCollapsingCoefficient = storiesCollapsingCoefficient,
            stories = stories,
            imageModel = imageModel,
            contentDescription = contentDescription,
            onStoryClick = onStoryClick,
            placeholder = placeholder,
            errorPlaceholder = errorPlaceholder
        )
    }
}

@Composable
private fun CollapsableLazyStoriesShimmerRow(
    modifier: Modifier = Modifier,
    placeholdersCount: Int,
    topBarState: CollapsingTopBarState,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(Dimens.zeroDp),
    @FloatRange(to = 1.0)
    storiesOverlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    storiesArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    storyShape: Shape = CircleShape,
    storyBackgroundColor: Color = Color.Transparent,
    storyBorder: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    storyBorderPadding: PaddingValues = PaddingValues(StoryDimens.borderPadding),
    miniStoryMaxHeight: Dp = StoryDimens.miniStoriesMaxHeight,
    storyMaxHeight: Dp = StoryDimens.maxHeight,
    storiesCollapsingCoefficient: Int = StoryConstants.STORIES_COLLAPSING_COEFFICIENT,
    loadingPlaceholder: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .shimmer()
        )
    }
) {
    CollapsableLazyStoriesRow(
        stories = List(placeholdersCount) { null },
        imageModel = { it },
        onStoryClick = {
            // Do nothing
        },
        contentPadding = contentPadding,
        modifier = modifier,
        storiesOverlapFraction = storiesOverlapFraction,
        storiesArrangement = storiesArrangement,
        storyShape = storyShape,
        storyBackgroundColor = storyBackgroundColor,
        storyBorder = storyBorder,
        storyBorderPadding = storyBorderPadding,
        miniStoryMaxHeight = miniStoryMaxHeight,
        storyMaxHeight = storyMaxHeight,
        topBarState = topBarState,
        lazyListState = lazyListState,
        storiesCollapsingCoefficient = storiesCollapsingCoefficient,
        placeholder = loadingPlaceholder
    )
}

@Composable
private fun <T> CollapsableLazyStoriesRow(
    modifier: Modifier = Modifier,
    stories: List<T>,
    imageModel: (T) -> Any?,
    contentDescription: (T) -> String? = { null },
    onStoryClick: (story: T) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    topBarState: CollapsingTopBarState,
    contentPadding: PaddingValues = PaddingValues(Dimens.zeroDp),
    @FloatRange(to = 1.0)
    storiesOverlapFraction: Float = StoryConstants.STORIES_OVERLAP_FRACTION,
    storiesArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    storyShape: Shape = CircleShape,
    storyBorder: BorderStroke? = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
    storyBackgroundColor: Color = Color.Transparent,
    storyBorderPadding: PaddingValues = PaddingValues(StoryDimens.borderPadding),
    miniStoryMaxHeight: Dp = StoryDimens.miniStoriesMaxHeight,
    storyMaxHeight: Dp = StoryDimens.maxHeight,
    storiesCollapsingCoefficient: Int = StoryConstants.STORIES_COLLAPSING_COEFFICIENT,
    placeholder: @Composable () -> Unit = {},
    errorPlaceholder: @Composable () -> Unit = placeholder
) {
    LazyRow(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        horizontalArrangement = storiesArrangement
    ) {
        itemsIndexed(stories) { index, item ->
            StoryItem(
                modifier = Modifier.storyCollapseEffect(
                    topBarState = topBarState,
                    lazyListState = lazyListState,
                    itemIndex = index,
                    overlapFraction = storiesOverlapFraction,
                    minHeight = miniStoryMaxHeight,
                    maxHeight = storyMaxHeight
                ),
                storyImageModel = imageModel(item),
                contentDescription = contentDescription(item),
                maxHeight = storyMaxHeight,
                placeholder = placeholder,
                errorPlaceholder = errorPlaceholder,
                shape = storyShape,
                border = storyBorder,
                backgroundColor = storyBackgroundColor,
                borderPadding = storyBorderPadding,
                onClick = {
                    onStoryClick(item)
                }
            )

            if (index == stories.lastIndex) {
                Spacer(modifier = Modifier.width(storyMaxHeight * storiesCollapsingCoefficient * topBarState.collapseFraction))
            }
        }
    }
}

@Composable
private fun Modifier.storyCollapseEffect(
    topBarState: CollapsingTopBarState,
    lazyListState: LazyListState,
    itemIndex: Int,
    overlapFraction: Float,
    minHeight: Dp,
    maxHeight: Dp
): Modifier {
    val xOffset by remember(itemIndex) {
        derivedStateOf {
            val relativeIndex = itemIndex - lazyListState.firstVisibleItemIndex
            val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset

            val baseOffset = -topBarState.minHeightPx * relativeIndex * topBarState.collapseFraction

            val scrolledOffset = if (itemIndex == firstVisibleItemIndex) {
                -firstVisibleItemScrollOffset * topBarState.collapseFraction
            } else {
                0f
            }

            (baseOffset + scrolledOffset) * overlapFraction
        }
    }

    val alpha by remember(itemIndex) {
        derivedStateOf {
            val relativeIndex = itemIndex - lazyListState.firstVisibleItemIndex
            (1 - (relativeIndex - 1) * topBarState.collapseFraction).coerceIn(0f, 1f)
        }
    }

    val size by remember(minHeight, maxHeight) {
        derivedStateOf { minHeight + ((maxHeight - minHeight) * topBarState.fraction) }
    }

    return this
        .offset { IntOffset(x = xOffset.roundToInt(), y = 0) }
        .alpha(alpha)
        .size(size)
}

@Preview
@Composable
private fun CollapsableLazyStoriesRowPreview() {
    CollapsableLazyStoriesRow(
        stories = listOf(null, null, null),
        imageModel = { it },
        onStoryClick = {},
        topBarState = rememberCollapsingTopBarState(StoryDimens.miniStoriesMaxHeight, StoryDimens.maxHeight)
    )
}

@Preview
@Composable
private fun CollapsableLazyStoriesRowLoadingPreview() {
    CollapsableLazyStoriesShimmerRow(
        placeholdersCount = 5,
        topBarState = rememberCollapsingTopBarState(StoryDimens.miniStoriesMaxHeight, StoryDimens.maxHeight)
    )
}