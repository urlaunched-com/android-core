package com.urlaunched.android.design.ui.videotutorial.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun VideoTapNavigator(modifier: Modifier = Modifier, onPreviousVideo: () -> Unit, onNextVideo: () -> Unit) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onPreviousVideo
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onNextVideo
                )
        )
    }
}