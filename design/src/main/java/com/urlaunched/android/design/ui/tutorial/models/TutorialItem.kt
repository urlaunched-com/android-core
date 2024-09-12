package com.urlaunched.android.design.ui.tutorial.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.time.Duration

data class TutorialItem(
    val layoutCoordinates: TutorialLayoutCoordinates,
    val content: @Composable (modifier: Modifier, showNextTutorialItem: () -> Unit) -> Unit,
    val position: TutorialItemPosition = TutorialItemPosition.BOTTOM,
    val onItemSwitch: () -> Unit = {},
    val additionalOffsetX: Int = 0,
    val additionalOffsetY: Int = 0,
    val defaultCircleScale: Float = 1.3f,
    // use this param when tutorials should be on same position
    val needToShowContentAnimation: Boolean = true,
    val tutorialAnimationDelay: Duration = Duration.ZERO
)