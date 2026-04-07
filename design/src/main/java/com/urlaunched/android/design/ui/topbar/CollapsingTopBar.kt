package com.urlaunched.android.design.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun CollapsingTopBar(
    modifier: Modifier = Modifier,
    state: CollapsingTopBarState,
    containerColor: Color = Color.Transparent,
    topBar: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(connection = state.nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .height(state.heightDp),
            content = topBar
        )

        Box(
            modifier = Modifier
                .offset(y = state.heightDp)
                .fillMaxWidth(),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollapsingTopBarPreview() {
    val topBarState = rememberCollapsingTopBarState(50.dp, 150.dp)

    CollapsingTopBar(
        state = topBarState,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.spacingNormal)
                    .background(Color.Gray, RoundedCornerShape(Dimens.cornerRadiusNormal))
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(Dimens.spacingNormal),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingNormal)
        ) {
            items(100) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.spacingExtraLarge)
                        .background(Color.LightGray, RoundedCornerShape(Dimens.cornerRadiusNormal))
                )
            }
        }
    }
}