package com.urlaunched.android.design.ui.pulltorefresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.urlaunched.android.design.ui.modifiers.ifNotNull

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PullToRefreshBox(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    state: PullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh),
    containerColor: Color = Color.Transparent,
    topBar: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(containerColor)
            .pullRefresh(state)
            .ifNotNull(scrollBehavior) { behavior ->
                Modifier.nestedScroll(behavior.nestedScrollConnection)
            },
        content = {
            Box(modifier = Modifier.background(containerColor)) {
                topBar()
            }

            Box(
                modifier = Modifier
                    .clip(RectangleShape)
                    .background(containerColor)
            ) {
                content()

                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    refreshing = isRefreshing,
                    state = state,
                    backgroundColor = containerColor
                )
            }
        }
    )
}