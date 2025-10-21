package com.urlaunched.android.design.ui.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.shimmer.shimmer
import com.urlaunched.android.design.ui.stories.constants.StoryConstants
import com.urlaunched.android.design.ui.stories.constants.StoryDimens
import com.urlaunched.android.design.ui.stories.model.MiniStoriesConfig
import com.urlaunched.android.design.ui.stories.model.MiniStoriesStyle
import com.urlaunched.android.design.ui.stories.model.StoriesConfig
import com.urlaunched.android.design.ui.stories.model.StoriesStyle
import com.urlaunched.android.design.ui.topbar.CollapsingTopBar
import com.urlaunched.android.design.ui.topbar.rememberCollapsingTopBarState

@Composable
fun <T> CollapsingStoriesTopBar(
    modifier: Modifier = Modifier,
    stories: List<T>,
    onStoryClick: (story: T) -> Unit,
    topBarMinHeight: Dp,
    topBarMaxHeight: Dp,
    imageModel: (T) -> Any? = { it },
    contentDescription: (T) -> String? = { null },
    isLoading: Boolean = false,
    showStories: Boolean = true,
    storiesListState: LazyListState = rememberLazyListState(),
    topBarContainerColor: Color = Color.Transparent,
    hideBigStoryFraction: Float = StoryConstants.HIDE_STORY_FRACTION,
    showMiniStoryFraction: Float = StoryConstants.SHOW_MINI_STORY_FRACTION,
    storyShape: Shape = CircleShape,
    storiesStyle: StoriesStyle = StoriesStyle(),
    storiesConfig: StoriesConfig = StoriesConfig(),
    miniStoriesConfig: MiniStoriesConfig = MiniStoriesConfig(),
    miniStoriesStyle: MiniStoriesStyle = MiniStoriesStyle(),
    onMiniStoriesClick: () -> Unit = {},
    storyPlaceholder: @Composable () -> Unit = {},
    storyErrorPlaceholder: @Composable () -> Unit = storyPlaceholder,
    loadingPlaceholder: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .shimmer()
        )
    },
    topBar: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val topBarState = rememberCollapsingTopBarState(topBarMinHeight, topBarMaxHeight)

    val showBigStories by remember(hideBigStoryFraction) {
        derivedStateOf { topBarState.fraction > hideBigStoryFraction }
    }

    val showMiniStories by remember(showMiniStoryFraction) {
        derivedStateOf { topBarState.fraction < showMiniStoryFraction }
    }

    CollapsingTopBar(
        modifier = modifier,
        state = topBarState,
        content = content,
        containerColor = topBarContainerColor,
        topBar = {
            topBar()

            if (showStories) {
                if (showMiniStories) {
                    MiniStories(
                        modifier = Modifier
                            .offset(x = miniStoriesStyle.storiesOffset.x, y = miniStoriesStyle.storiesOffset.y)
                            .alpha(topBarState.collapseFraction)
                            .align(Alignment.BottomStart),
                        stories = stories,
                        shape = storyShape,
                        imageModel = imageModel,
                        onStoriesClick = onMiniStoriesClick,
                        storySize = miniStoriesStyle.maxHeight,
                        storyOverlapFraction = miniStoriesConfig.overlapFraction,
                        maxHiddenCount = miniStoriesConfig.maxHiddenCount,
                        visibleImagesCount = miniStoriesConfig.visibleImagesCount,
                        hiddenIndicatorBackgroundColor = miniStoriesStyle.hiddenIndicatorBackgroundColor,
                        hiddenIndicatorTextStyle = miniStoriesStyle.hiddenIndicatorTextStyle,
                        storyBorder = miniStoriesStyle.borderStroke,
                        placeholder = storyPlaceholder,
                        errorPlaceholder = storyErrorPlaceholder,
                        loadingPlaceholder = loadingPlaceholder,
                        isLoading = isLoading,
                        placeholdersCount = miniStoriesConfig.placeholdersCount,
                        contentDescription = contentDescription
                    )
                }

                if (showBigStories && (isLoading || stories.isNotEmpty())) {
                    CollapsingStoriesList(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart),
                        stories = stories,
                        isLoading = isLoading,
                        placeholdersCount = storiesConfig.placeholdersCount,
                        imageModel = imageModel,
                        contentDescription = contentDescription,
                        onStoryClick = onStoryClick,
                        lazyListState = storiesListState,
                        topBarState = topBarState,
                        storyBackgroundColor = storiesStyle.storyBackgroundColor,
                        contentPadding = storiesStyle.storiesRowPadding,
                        storiesOverlapFraction = storiesConfig.overlapFraction,
                        storiesArrangement = storiesStyle.arrangement,
                        storyShape = storyShape,
                        storyBorder = storiesStyle.borderStroke,
                        storyBorderPadding = storiesStyle.storyBorderPadding,
                        miniStoryMaxHeight = storiesStyle.minHeight,
                        storyMaxHeight = storiesStyle.maxHeight,
                        storiesCollapsingCoefficient = storiesConfig.collapsingCoefficient,
                        placeholder = storyPlaceholder,
                        errorPlaceholder = storyErrorPlaceholder,
                        loadingPlaceholder = loadingPlaceholder
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CollapsingTopBarPreview() {
    val stories = remember {
        listOf(
            "https://cdn.pixabay.com/photo/2025/09/04/05/38/leaves-9814751_1280.jpg",
            "https://cdn.pixabay.com/photo/2025/08/09/09/05/nature-9764183_1280.jpg",
            "https://cdn.pixabay.com/photo/2025/09/21/12/12/crocodile-9846352_1280.jpg",
            "https://cdn.pixabay.com/photo/2022/08/16/16/46/daylily-7390789_1280.jpg",
            "https://cdn.pixabay.com/photo/2022/09/17/17/54/monkey-7461483_1280.jpg"
        )
    }
    val isLoading = remember { false }
    val minHeight = remember { 64.dp }
    val maxHeight = remember(minHeight) {
        if (isLoading || stories.isNotEmpty()) {
            minHeight + StoryDimens.maxHeight + Dimens.spacingNormal * 2
        } else {
            minHeight + Dimens.spacingNormal
        }
    }

    CollapsingStoriesTopBar(
        stories = stories,
        isLoading = isLoading,
        topBarMinHeight = minHeight,
        topBarMaxHeight = maxHeight,
        modifier = Modifier
            .statusBarsPadding()
            .background(Color.White),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.spacingNormalSpecial,
                        vertical = Dimens.spacingSmall
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.size(48.dp))

                Text(
                    text = "Catalogue",
                    style = MaterialTheme.typography.headlineSmall
                )

                FilledTonalIconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null
                    )
                }
            }
        },
        onStoryClick = {}
    ) {
        LazyColumn(
            contentPadding = PaddingValues(Dimens.spacingNormal),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingNormal)
        ) {
            items(10) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.LightGray, RoundedCornerShape(Dimens.cornerRadiusBig))
                )
            }
        }
    }
}