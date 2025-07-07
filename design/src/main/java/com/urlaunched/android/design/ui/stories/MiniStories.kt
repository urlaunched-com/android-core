package com.urlaunched.android.design.ui.stories
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.ui.image.SubcomposeAsyncImageScope

@Composable
fun MiniStories(
    modifier: Modifier = Modifier,
    stories: List<Any?>,
    miniStoriesMaxHeight: Dp,
    showMiniStoriesCount: Int,
    borderWidth: Dp = StoryDimens.border,
    borderColor: Color = Color.Gray,
    hiddenStoriesItemColor: Color,
    hiddenStoriesTextStyle: TextStyle,
    maxHiddenMiniStoriesCount: Int,
    onStoriesClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onStoriesClick)
    ) {
        stories.forEachIndexed { index, story ->
            when {
                index < showMiniStoriesCount -> {
                    SubcomposeAsyncImageScope(
                        modifier = Modifier
                            .padding(start = (miniStoriesMaxHeight / 2 * index))
                            .heightIn(max = miniStoriesMaxHeight)
                            .aspectRatio(1f)
                            .border(
                                width = borderWidth,
                                color = borderColor,
                                shape = CircleShape
                            )
                            .clip(CircleShape),
                        model = story,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        shape = CircleShape
                    )
                }

                index == showMiniStoriesCount -> {
                    Box(
                        modifier = Modifier
                            .padding(start = (miniStoriesMaxHeight / 2 * index))
                            .heightIn(max = miniStoriesMaxHeight)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(hiddenStoriesItemColor)
                    ) {
                        val hiddenStoriesCount by remember(stories) {
                            mutableIntStateOf(stories.size - showMiniStoriesCount)
                        }

                        Text(
                            text = if (hiddenStoriesCount <= maxHiddenMiniStoriesCount) {
                                "+$hiddenStoriesCount"
                            } else {
                                "$maxHiddenMiniStoriesCount+"
                            },
                            style = hiddenStoriesTextStyle,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                else -> return@forEachIndexed
            }
        }
    }
}