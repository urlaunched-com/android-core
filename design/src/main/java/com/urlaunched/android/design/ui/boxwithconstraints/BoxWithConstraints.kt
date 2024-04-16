package com.urlaunched.android.design.ui.boxwithconstraints

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints as ComposeBoxWithConstraints

@Composable
fun BoxWithConstraints(modifier: Modifier = Modifier, content: @Composable (maxHeight: Dp, maxWidth: Dp) -> Unit) {
    if (LocalInspectionMode.current) {
        content(1000.dp, 500.dp)
    } else {
        ComposeBoxWithConstraints(
            modifier = modifier
        ) {
            content(maxHeight, maxWidth)
        }
    }
}