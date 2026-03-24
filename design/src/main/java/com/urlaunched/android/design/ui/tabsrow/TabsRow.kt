package com.urlaunched.android.design.ui.tabsrow

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.zIndex
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.modifiers.ifNotNull
import com.urlaunched.android.design.ui.tabsrow.constants.TabsRowConstants
import com.urlaunched.android.design.ui.tabsrow.constants.TabsRowDimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsRow(
    modifier: Modifier = Modifier,
    pages: List<String>,
    pagerState: PagerState,
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
    onTabChange: (index: Int) -> Unit = {},
    indicator: @Composable ((page: Int) -> Unit)? = null,
    startContent: @Composable (RowScope.() -> Unit)? = null,
    endContent: @Composable (RowScope.() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        startContent?.let {
            startContent()
        }

        BoxWithConstraints(
            modifier = Modifier
                .clip(CircleShape)
                .weight(TabsRowConstants.TABS_WEIGHT)
        ) {
            TabRow(
                modifier = Modifier
                    .height(tabsHeight)
                    .clip(CircleShape),
                selectedTabIndex = pagerState.currentPage,
                containerColor = containerColor,
                indicator = { tabPositions: List<TabPosition> ->
                    CustomIndicator(
                        tabPositions = tabPositions,
                        pagerState = pagerState,
                        colors = colors,
                        elevation = indicatorShadow,
                        maxWidth = maxWidth,
                        height = indicatorHeight
                    )
                },
                // Remove divider
                divider = {}
            ) {
                pages.forEachIndexed { index, text ->
                    val selected = pagerState.currentPage == index
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Tab(
                            modifier = Modifier.zIndex(TabsRowConstants.TAB_ZINDEX),
                            text = {
                                if (indicator != null) {
                                    indicator(index)
                                } else {
                                    BasicText(
                                        text = text,
                                        style = if (selected) {
                                            selectedTextStyle.copy(
                                                textAlign = TextAlign.Center,
                                                color = selectedTextColor
                                            )
                                        } else {
                                            unselectedTextStyle.copy(
                                                textAlign = TextAlign.Center,
                                                color = unselectedTextColor
                                            )
                                        },
                                        maxLines = 1,
                                        autoSize = TextAutoSize.StepBased(
                                            maxFontSize = if (selected) {
                                                selectedTextStyle.fontSize
                                            } else {
                                                unselectedTextStyle.fontSize
                                            },
                                            minFontSize = minFontSize
                                        )
                                    )
                                }
                            },
                            enabled = enabled,
                            selected = selected,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }

                                onTabChange(index)
                            },
                            selectedContentColor = containerColor,
                            unselectedContentColor = containerColor,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    }
                }
            }
        }

        endContent?.let {
            endContent()
        }
    }
}

@Composable
private fun CustomIndicator(
    modifier: Modifier = Modifier,
    tabPositions: List<TabPosition>,
    pagerState: PagerState,
    colors: List<Color>,
    maxWidth: Dp,
    height: Dp,
    elevation: Dp? = null
) {
    val transition =
        updateTransition(
            targetState = pagerState.currentPage,
            label = TabsRowConstants.PAGER_INDICATOR_PAGE_TRANSITION_LABEL
        )
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = TabsRowConstants.PAGER_INDICATOR_START_POSITION_LABEL
    ) {
        tabPositions[it].left
    }

    val color by transition.animateColor(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = TabsRowConstants.PAGER_INDICATOR_COLOR_LABEL
    ) { page ->
        colors.getOrElse(index = page, defaultValue = { colors[0] })
    }

    Box(
        modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.CenterStart)
            .width(maxWidth / pagerState.pageCount)
            .padding(horizontal = Dimens.spacingTinyHalf)
            .ifNotNull(elevation) {
                elevation?.let { Modifier.shadow(elevation = elevation, shape = CircleShape) } ?: Modifier
            }
            .clip(CircleShape)
            .background(color)
            .height(height)
            .zIndex(TabsRowConstants.INDICATOR_ZINDEX)
    )
}

@Preview
@Composable
private fun TabsRowPreview() {
    TabsRow(
        pages = listOf("Test 1", "Test 2", "Test 3"),
        pagerState = rememberPagerState(0, pageCount = { 3 }),
        onTabChange = { },
        startContent = { -> }
    )
}