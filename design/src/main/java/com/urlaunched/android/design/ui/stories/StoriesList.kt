package com.urlaunched.android.design.ui.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.shimmer.shimmer
import com.urlaunched.android.design.ui.topbar.CollapsingTopBarState

data class StoriesListConfig(
    val modifier: Modifier = Modifier,
    val storyItemModifier: Modifier = Modifier,
    val isLoading: Boolean,
    val stories: List<Any?>,
    val contentPadding: PaddingValues = PaddingValues(Dimens.zeroDp),
    val storiesOverlapFraction: Float = 0.4f,
    val miniStoryMaxHeight: Dp = StoryDimens.miniStoriesMaxHeight,
    val storyMaxHeight: Dp = StoryDimens.maxHeight,
    val storyBackgroundColor: Color = Color.Gray,
    val storiesCollapsingCoefficient: Int = 4,
    val placeholder: @Composable () -> Unit = {},
    val errorPlaceholder: @Composable () -> Unit = placeholder
)

@Composable
fun StoriesList(
    topBarState: CollapsingTopBarState?,
    storiesListConfig: StoriesListConfig,
    onStoryClick: (story: Any) -> Unit
) {
    with(storiesListConfig) {
        if (isLoading) {
            PromoStoryItemPlaceholder(
                modifier = modifier,
                contentPadding = contentPadding,
                storiesOverlapFraction = storiesOverlapFraction,
                topBarState = topBarState,
                miniStoryMaxHeight = miniStoryMaxHeight,
                storyMaxHeight = storyMaxHeight
            )
        } else {
            PromoStoriesLazyRow(
                modifier = modifier,
                storyItemModifier = storyItemModifier,
                topBarState = topBarState,
                contentPadding = contentPadding,
                stories = stories,
                onItemClick = onStoryClick,
                storiesOverlapFraction = storiesOverlapFraction,
                miniStoryMaxHeight = miniStoryMaxHeight,
                storyMaxHeight = storyMaxHeight,
                storyBackgroundColor = storyBackgroundColor,
                storiesCollapsingCoefficient = storiesCollapsingCoefficient,
                placeholder = storiesListConfig.placeholder,
                errorPlaceholder = storiesListConfig.errorPlaceholder
            )
        }
    }
}

@Composable
private fun PromoStoryItemPlaceholder(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.zeroDp),
    storiesOverlapFraction: Float,
    topBarState: CollapsingTopBarState?,
    miniStoryMaxHeight: Dp,
    storyMaxHeight: Dp
) {
    PromoStoriesLazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        stories = listOf("", "", "", "", "", ""),
        storiesOverlapFraction = storiesOverlapFraction,
        onItemClick = {
            // Do nothing
        },
        placeholder = {},
        errorPlaceholder = {},
        storyItemModifier = Modifier.shimmer(),
        topBarState = topBarState,
        miniStoryMaxHeight = miniStoryMaxHeight,
        storyMaxHeight = storyMaxHeight,
        storyBackgroundColor = Color.Transparent,
        storiesCollapsingCoefficient = 0
    )
}

@Composable
private fun PromoStoriesLazyRow(
    modifier: Modifier = Modifier,
    storyItemModifier: Modifier = Modifier,
    stories: List<Any?>,
    onItemClick: (story: Any) -> Unit,
    topBarState: CollapsingTopBarState? = null,
    contentPadding: PaddingValues = PaddingValues(Dimens.zeroDp),
    storiesOverlapFraction: Float,
    miniStoryMaxHeight: Dp,
    storyMaxHeight: Dp,
    storyBackgroundColor: Color,
    storiesCollapsingCoefficient: Int,
    placeholder: @Composable () -> Unit,
    errorPlaceholder: @Composable () -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
    ) {
        itemsIndexed(stories) { index, item ->
            if (topBarState == null) {
                item?.let {
                    PromoStoryItem(
                        modifier = Modifier
                            .clickable { onItemClick(it) }
                            .then(storyItemModifier),
                        storyImage = it,
                        maxHeight = StoryDimens.maxHeight,
                        placeholder = placeholder,
                        errorPlaceholder = errorPlaceholder
                    )
                }
            } else {
                val collapseValue = 1 - topBarState.fraction

                val firstVisibleItemScrollOffset by remember {
                    derivedStateOf { lazyListState.firstVisibleItemScrollOffset }
                }

                val xOffset by remember(topBarState.heightPx, firstVisibleItemScrollOffset) {
                    val offsetX =
                        -topBarState.minHeightPx * (index - lazyListState.firstVisibleItemIndex) * collapseValue

                    derivedStateOf { (offsetX * storiesOverlapFraction) }
                }

                val alpha by remember(topBarState.heightPx) {
                    val alpha = 1 - (index + 1) * collapseValue / 2

                    derivedStateOf { alpha.coerceIn(0f, 1f) }
                }

                val density = LocalDensity.current
                val storiesSize = (storyMaxHeight - miniStoryMaxHeight) * topBarState.fraction

                val firstVisibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

                Row {
                    if (firstVisibleItemIndex == index) {
                        Spacer(
                            modifier = Modifier.padding(
                                start = with(density) {
                                    (lazyListState.firstVisibleItemScrollOffset * collapseValue).toDp()
                                }
                            )
                        )
                    }
                    item?.let {
                        PromoStoryItem(
                            modifier = Modifier
                                .offset(x = with(density) { xOffset.toDp() })
                                .size((miniStoryMaxHeight + storiesSize))
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable { onItemClick(item) }
                                .alpha(alpha)
                                .background(storyBackgroundColor)
                                .then(storyItemModifier),
                            storyImage = item,
                            maxHeight = storyMaxHeight,
                            placeholder = placeholder,
                            errorPlaceholder = errorPlaceholder
                        )
                    }

                    if (index + 1 == stories.size) {
                        Spacer(modifier = Modifier.padding(start = storyMaxHeight * storiesCollapsingCoefficient * collapseValue))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PromoStoryListPreview() {
    StoriesList(
        storiesListConfig = StoriesListConfig(
            stories = listOf(),
            isLoading = false
        ),
        topBarState = null,
        onStoryClick = {}
    )
}