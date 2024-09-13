package com.urlaunched.android.design.ui.tutorial.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import com.urlaunched.android.design.ui.tutorial.models.TutorialItem
import com.urlaunched.android.design.ui.tutorial.models.TutorialItemPosition
import com.urlaunched.android.design.ui.tutorial.models.TutorialProperties
import com.urlaunched.android.design.ui.tutorial.utils.TutorialUtils.getMaxSide
import kotlinx.coroutines.delay

@Composable
fun TutorialContainer(
    tutorialItems: List<TutorialItem>,
    showTutorialsAutomatically: Boolean = false,
    backgroundColor: Color = Color.Black.copy(alpha = 0.85f),
    tutorialProperties: TutorialProperties = TutorialProperties(),
    content: @Composable (showNextTutorialItem: () -> Unit) -> Unit
) {
    var currentTutorialItemIndex by remember { mutableIntStateOf(0) }

    var currentTutorialItem: TutorialItem? = remember(tutorialItems, currentTutorialItemIndex) {
        tutorialItems.getOrNull(currentTutorialItemIndex)
    }
    val defaultCircleScale = currentTutorialItem?.defaultCircleScale ?: 0f

    val isFirstItem = tutorialItems.indexOf(currentTutorialItem) == 0
    val isLastItem = currentTutorialItemIndex == tutorialItems.size - 1

    val circleRadius = remember(currentTutorialItem) {
        Animatable(
            if (currentTutorialItem?.needToShowContentAnimation == true) {
                0f
            } else {
                (getMaxSide(currentTutorialItem?.layoutCoordinates) / 2) * defaultCircleScale
            }
        )
    }

    // This alpha for smooth display tutorial content around the circle
    val contentAlpha = remember(currentTutorialItem) { Animatable(0f) }

    LaunchedEffect(currentTutorialItem) {
        currentTutorialItem?.let { tutorialItem ->
            // This check for avoid redundant circle animation when tutorialLayout coordinates are same
            if (tutorialItem.needToShowContentAnimation) {
                circleRadius.animateTo(
                    targetValue = (getMaxSide(tutorialItem.layoutCoordinates) / 2) * defaultCircleScale,
                    animationSpec = tween(
                        durationMillis = tutorialProperties.circleRadiusAnimationDuration,
                        // this delay before start first tutorial item
                        delayMillis = if (isFirstItem) tutorialProperties.delayBeforeStartTutorial else tutorialItem.tutorialAnimationDelay.inWholeMilliseconds.toInt()
                    )
                )
            }

            contentAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = tutorialProperties.contentAlphaAnimationDuration,
                    delayMillis = 0
                )
            )
        }
    }

    // Use this lambda to switch tutorial items
    val showNextTutorialItem: () -> Unit = {
        currentTutorialItem = tutorialItems.getOrNull(++currentTutorialItemIndex)
    }

    // showTutorialsAutomatically use in case when we need auto switch between tutorial items
    if (showTutorialsAutomatically) {
        LaunchedEffect(currentTutorialItemIndex) {
            delay(
                if (isFirstItem) {
                    tutorialProperties.defaultTutorialPreviewTime + tutorialProperties.delayBeforeStartTutorial
                } else {
                    tutorialProperties.defaultTutorialPreviewTime
                }
            )
            if (!isLastItem) showNextTutorialItem()
        }
    }

    // Do something with content when items switches
    LaunchedEffect(currentTutorialItemIndex) {
        currentTutorialItem?.onItemSwitch?.let { it() }
    }

    Box {
        currentTutorialItem?.let { tutorialItem ->
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .graphicsLayer {
                        alpha = .99f
                    }
            ) {
                drawRect(
                    color = backgroundColor
                )

                drawCircle(
                    color = Color.Transparent,
                    blendMode = BlendMode.Clear,
                    center = tutorialItem.layoutCoordinates.center,
                    radius = circleRadius.value
                )
            }
        }

        content(showNextTutorialItem)
    }

    currentTutorialItem?.let { tutorialItem ->
        val layoutCoordinates = tutorialItem.layoutCoordinates
        val radius = (getMaxSide(layoutCoordinates) / 2) * defaultCircleScale

        var tutorialItemWidth by remember { mutableFloatStateOf(0f) }
        var tutorialItemHeight by remember { mutableFloatStateOf(0f) }

        tutorialItem.content(
            Modifier
                .zIndex(2f)
                .onGloballyPositioned { contentLayoutCoordinates ->
                    tutorialItemWidth = contentLayoutCoordinates.size.width.toFloat()
                    tutorialItemHeight = contentLayoutCoordinates.size.height.toFloat()
                }
                .graphicsLayer(
                    translationX = when (tutorialItem.position) {
                        TutorialItemPosition.LEFT -> layoutCoordinates.center.x - radius - tutorialItemWidth + tutorialItem.additionalOffsetX
                        TutorialItemPosition.RIGHT -> layoutCoordinates.center.x + radius + tutorialItem.additionalOffsetX
                        TutorialItemPosition.TOP -> layoutCoordinates.center.x - tutorialItemWidth / 2 + tutorialItem.additionalOffsetX
                        TutorialItemPosition.BOTTOM -> layoutCoordinates.center.x - tutorialItemWidth / 2 + tutorialItem.additionalOffsetX
                    },
                    translationY = when (tutorialItem.position) {
                        TutorialItemPosition.LEFT -> layoutCoordinates.center.y - tutorialItemHeight / 2 + tutorialItem.additionalOffsetY
                        TutorialItemPosition.RIGHT -> layoutCoordinates.center.y - tutorialItemHeight / 2 + tutorialItem.additionalOffsetY
                        TutorialItemPosition.TOP -> layoutCoordinates.center.y - radius - tutorialItemHeight + tutorialItem.additionalOffsetY
                        TutorialItemPosition.BOTTOM -> layoutCoordinates.center.y + radius + tutorialItem.additionalOffsetY
                    }
                )
                .alpha(contentAlpha.value),
            showNextTutorialItem
        )
    }
}