package com.urlaunched.android.design.ui.tabsrow

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.urlaunched.android.design.ui.tabsrow.components.SynchronizedLazyGridScrollState
import com.urlaunched.android.design.ui.tabsrow.constants.TabsRowDimens

@Composable
fun AnimatedTabsRowScaffold(
    modifier: Modifier = Modifier,
    synchronizedLazyGridScrollState: SynchronizedLazyGridScrollState,
    pagerState: PagerState,
    pages: List<String>,
    tabsPadding: PaddingValues = PaddingValues(),
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
    tabIndicator: @Composable ((page: Int) -> Unit)? = null,
    tabStartContent: @Composable (RowScope.() -> Unit)? = null,
    tabEndContent: @Composable (RowScope.() -> Unit)? = null,
    topBarContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            AnimatedTabsRow(
                synchronizedLazyGridScrollState = synchronizedLazyGridScrollState,
                pagerState = pagerState,
                pages = pages,
                topBarContent = topBarContent,
                contentPadding = tabsPadding,
                tabsHeight = tabsHeight,
                indicatorHeight = indicatorHeight,
                indicatorShadow = indicatorShadow,
                enabled = enabled,
                colors = colors,
                selectedTextColor = selectedTextColor,
                unselectedTextColor = unselectedTextColor,
                containerColor = containerColor,
                backgroundColor = backgroundColor,
                selectedTextStyle = selectedTextStyle,
                unselectedTextStyle = unselectedTextStyle,
                minFontSize = minFontSize,
                onTabChange = onTabChange,
                indicator = tabIndicator,
                startContent = tabStartContent,
                endContent = tabEndContent
            )

            CompositionLocalProvider(LocalOverscrollFactory provides null) {
                content()
            }
        }
    ) { measurables, constraints ->
        val offset = synchronizedLazyGridScrollState.targetOffset
        val topBarPlaceable = measurables[0].measure(constraints)
        val contentPlaceable = measurables[1].measure(
            constraints.copy(maxHeight = constraints.maxHeight - topBarPlaceable.height + offset)
        )

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            contentPlaceable.placeRelative(
                x = 0,
                y = topBarPlaceable.height - offset
            )

            topBarPlaceable.placeRelative(
                x = 0,
                y = 0
            )
        }
    }
}