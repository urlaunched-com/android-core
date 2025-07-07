package com.urlaunched.android.design.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.stories.MiniStories
import com.urlaunched.android.design.ui.stories.StoriesList
import com.urlaunched.android.design.ui.stories.StoriesListConfig
import com.urlaunched.android.design.ui.stories.StoryDimens

data class MiniStoriesConfig(
    val borderWidth: Dp,
    val borderColor: Color,
    val hiddenStoriesItemColor: Color,
    val hiddenStoriesTextStyle: TextStyle,
    val showMiniStoriesCount: Int = 2,
    val maxHiddenMiniStoriesCount: Int = 10,
    val miniStoriesVerticalPadding: Dp = Dimens.spacingNormal
)

@Composable
fun CollapsingTopBarStories(
    modifier: Modifier = Modifier,
    topBarContainerColor: Color = Color.Transparent,
    contentContainerColor: Color = Color.Transparent,
    hideBigStoryFraction: Float = 0.025f,
    showMiniStoryFraction: Float = 0.2f,
    storiesListConfig: StoriesListConfig,
    miniStoriesConfig: MiniStoriesConfig,
    topBar: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
    onStoryClick: (story: Any) -> Unit,
    onMiniStoriesClick: () -> Unit = {}
) {
    val topBarState =
        rememberCollapsingTopBarState(storiesListConfig.miniStoryMaxHeight, storiesListConfig.storyMaxHeight)
    CollapsingTopBar(
        modifier = modifier,
        state = topBarState,
        contentContainerColor = contentContainerColor,
        topBarContainerColor = topBarContainerColor,
        topBar = {
            topBar()

            val showPromoStories by remember(topBarState.fraction) {
                derivedStateOf { topBarState.fraction > hideBigStoryFraction }
            }

            if (showPromoStories && (storiesListConfig.isLoading || storiesListConfig.stories.isNotEmpty())) {
                StoriesList(
                    topBarState = topBarState,
                    storiesListConfig = storiesListConfig,
                    onStoryClick = onStoryClick
                )
            }

            if (topBarState.fraction < showMiniStoryFraction) {
                MiniStories(
                    modifier = Modifier
                        .offset(x = Dimens.spacingNormal, y = -miniStoriesConfig.miniStoriesVerticalPadding)
                        .alpha(1 - topBarState.fraction)
                        .align(Alignment.BottomStart),
                    stories = storiesListConfig.stories,
                    onStoriesClick = onMiniStoriesClick,
                    miniStoriesMaxHeight = storiesListConfig.miniStoryMaxHeight,
                    maxHiddenMiniStoriesCount = miniStoriesConfig.maxHiddenMiniStoriesCount,
                    showMiniStoriesCount = miniStoriesConfig.showMiniStoriesCount,
                    borderWidth = miniStoriesConfig.borderWidth,
                    borderColor = miniStoriesConfig.borderColor,
                    hiddenStoriesItemColor = miniStoriesConfig.hiddenStoriesItemColor,
                    hiddenStoriesTextStyle = miniStoriesConfig.hiddenStoriesTextStyle

                )
            }
        }
    ) {
        content()
    }
}

@Preview
@Composable
private fun CollapsingTopBarPreview() {
    CollapsingTopBarStories(
        modifier = Modifier.statusBarsPadding(),
        storiesListConfig = StoriesListConfig(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth(),
            storyItemModifier = Modifier.border(color = Color.Green, width = StoryDimens.border, shape = CircleShape),
            contentPadding = PaddingValues(
                horizontal = Dimens.spacingNormal,
                vertical = Dimens.spacingNormal
            ),
            isLoading = false,
            stories = listOf("https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg", "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg", "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg", "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg", "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg")
        ),
        miniStoriesConfig = MiniStoriesConfig(
            borderWidth = StoryDimens.border,
            borderColor = Color.Green,
            hiddenStoriesItemColor = Color.Yellow,
            hiddenStoriesTextStyle = TextStyle(),
            miniStoriesVerticalPadding = 0.dp,
            showMiniStoriesCount = 4
        ),
        topBar = {},
        content = {
            LazyColumn {
                items(10) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth().height(250.dp).background(Color.Red))
                }
            }
        },
        onMiniStoriesClick = {},
        onStoryClick = {}
    )
}