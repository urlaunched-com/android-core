package com.urlaunched.android.design.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.urlaunched.android.design.extensions.toPx

@Composable
fun CollapsingTopBar(
    modifier: Modifier = Modifier,
    state: CollapsingTopBarState,
    topBarContainerColor: Color = Color.Transparent,
    contentContainerColor: Color = Color.Transparent,
    topBar: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(connection = state.scrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarContainerColor)
                .height(state.heightDp)
        ) {
            topBar()
        }

        Box(
            modifier = Modifier
                .offset(y = state.heightDp)
                .background(contentContainerColor)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun CollapsingTopBarPreview() {
    val list = List(50) { it }

    val minHeight = 50.dp
    val maxHeight = 150.dp

    val topBarState = rememberCollapsingTopBarState(minHeight, maxHeight)

    CollapsingTopBar(
        state = topBarState,
        topBar = {
            val lazyState = rememberLazyListState()
            val collapseValue = 1 - topBarState.fraction

            LazyRow(
                state = lazyState,
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(list) { index, item ->
                    val minHeightPx = minHeight.toPx()

                    val firstVisibleItemScrollOffset by remember {
                        derivedStateOf { lazyState.firstVisibleItemScrollOffset }
                    }

                    val xOffset by remember(topBarState.heightPx, firstVisibleItemScrollOffset) {
                        val offsetX = -minHeightPx * (index - lazyState.firstVisibleItemIndex) * collapseValue
                        val firstItemOffsetX = (lazyState.firstVisibleItemScrollOffset) * collapseValue

                        derivedStateOf { (offsetX * 0.5f + firstItemOffsetX) }
                    }

                    val alpha by remember(topBarState.heightPx, firstVisibleItemScrollOffset) {
                        val alpha = 1 - (index - lazyState.firstVisibleItemIndex - 1) * collapseValue

                        derivedStateOf { alpha.coerceIn(0f, 1f) }
                    }

                    val density = LocalDensity.current

                    Box(
                        modifier = Modifier
                            .offset(x = with(density) { xOffset.toDp() })
                            .alpha(alpha)
                            .size((32.dp + 48.dp * topBarState.fraction))
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(100) {
                Text(
                    text = "Item $it",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}