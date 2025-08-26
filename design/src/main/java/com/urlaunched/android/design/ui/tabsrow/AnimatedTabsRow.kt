package com.urlaunched.android.design.ui.tabsrow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.tabsrow.components.SynchronizedLazyGridScrollState
import com.urlaunched.android.design.ui.tabsrow.components.rememberScrollProgress
import com.urlaunched.android.design.ui.tabsrow.components.rememberSynchronizedLazyGridScrollStates
import com.urlaunched.android.design.ui.tabsrow.components.verticalOffset
import com.urlaunched.android.design.ui.tabsrow.constants.TabsRowDimens
import kotlin.math.max

@Composable
fun AnimatedTabsRow(
    modifier: Modifier = Modifier,
    synchronizedLazyGridScrollState: SynchronizedLazyGridScrollState,
    pagerState: PagerState,
    topBarContent: @Composable () -> Unit,
    pages: List<String>,
    contentPadding: PaddingValues = PaddingValues(),
    tabsHeight: Dp = TabsRowDimens.pagerTabsHeight,
    indicatorHeight: Dp = TabsRowDimens.pagerTabsIndicatorHeight,
    indicatorShadow: Dp? = null,
    enabled: Boolean = true,
    colors: List<Color> = listOf(Color.DarkGray),
    selectedTextColor: Color = Color.White,
    unselectedTextColor: Color = Color.Gray,
    containerColor: Color = Color.White,
    selectedTextStyle: TextStyle = Typography().titleSmall,
    unselectedTextStyle: TextStyle = Typography().labelLarge,
    minFontSize: TextUnit = Typography().bodyMedium.fontSize,
    backgroundColor: Color = Color.White,
    onTabChange: (index: Int) -> Unit = {},
    indicator: @Composable ((page: Int) -> Unit)? = null,
    startContent: @Composable (RowScope.() -> Unit)? = null,
    endContent: @Composable (RowScope.() -> Unit)? = null
) {
    val firstTabScrollProgress by synchronizedLazyGridScrollState.firstTabState.rememberScrollProgress(Dimens.spacingNormalSpecial)
    val secondTabScrollProgress by synchronizedLazyGridScrollState.secondTabState.rememberScrollProgress(Dimens.spacingNormalSpecial)
    val progress by remember { derivedStateOf { max(firstTabScrollProgress, secondTabScrollProgress) } }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .verticalOffset(
                    firstScrollState = synchronizedLazyGridScrollState.firstTabState,
                    secondScrollState = synchronizedLazyGridScrollState.secondTabState,
                    initialOffset = Dimens.spacingNormal,
                    maxOffset = Dimens.zeroDp
                )
                .clip(
                    RoundedCornerShape(
                        bottomStart = Dimens.cornerRadiusNormal,
                        bottomEnd = Dimens.cornerRadiusNormal
                    )
                )
                .alpha(progress)
                .background(backgroundColor)
        )

        Column {
            topBarContent()

            TabsRow(
                modifier = Modifier
                    .padding(contentPadding)
                    .verticalOffset(
                        firstScrollState = synchronizedLazyGridScrollState.firstTabState,
                        secondScrollState = synchronizedLazyGridScrollState.secondTabState,
                        initialOffset = Dimens.spacingSmall,
                        maxOffset = Dimens.spacingSmall
                    ),
                pages = pages,
                containerColor = lerp(
                    start = backgroundColor,
                    stop = containerColor,
                    fraction = progress
                ),
                pagerState = pagerState,
                tabsHeight = tabsHeight,
                indicatorHeight = indicatorHeight,
                indicatorShadow = indicatorShadow,
                enabled = enabled,
                colors = colors,
                selectedTextColor = selectedTextColor,
                unselectedTextColor = unselectedTextColor,
                selectedTextStyle = selectedTextStyle,
                unselectedTextStyle = unselectedTextStyle,
                onTabChange = onTabChange,
                minFontSize = minFontSize,
                indicator = indicator,
                startContent = startContent,
                endContent = endContent
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xff999999)
@Composable
private fun AnimatedTabRowPreview() {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Box(modifier = Modifier.padding(bottom = Dimens.spacingBig)) {
        AnimatedTabsRow(
            modifier = Modifier.padding(horizontal = Dimens.spacingNormal),
            synchronizedLazyGridScrollState = rememberSynchronizedLazyGridScrollStates(
                spacing = Dimens.spacingNormalSpecial,
                initialOffset = Dimens.spacingNormal
            ),
            pagerState = pagerState,
            pages = listOf("First", "Second"),
            containerColor = Color.White,
            topBarContent = {
                Box(
                    modifier = Modifier.height(Dimens.spacingLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Top Bar")
                }
            }
        )
    }
}