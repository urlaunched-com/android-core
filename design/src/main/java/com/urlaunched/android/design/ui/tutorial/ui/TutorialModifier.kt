package com.urlaunched.android.design.ui.tutorial.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import com.urlaunched.android.design.ui.tutorial.models.TutorialLayoutCoordinates

fun Modifier.tutorial(
    setTutorialLayoutCoordinates: (tutorialLayoutCoordinates: TutorialLayoutCoordinates) -> Unit,
    additionalOffsetX: Int = 0,
    additionalOffsetY: Int = 0,
    additionalSize: Int = 0
) = onGloballyPositioned { layoutCoordinates ->
    setTutorialLayoutCoordinates(
        TutorialLayoutCoordinates(
            center = Offset(
                x = layoutCoordinates.size.width / 2f + layoutCoordinates.localToRoot(Offset(0f, 0f)).x + additionalOffsetX,
                y = layoutCoordinates.size.height / 2f + layoutCoordinates.localToRoot(Offset(0f, 0f)).y + additionalOffsetY
            ),
            width = layoutCoordinates.size.width.toFloat() + additionalSize,
            height = layoutCoordinates.size.height.toFloat() + additionalSize
        )
    )
}