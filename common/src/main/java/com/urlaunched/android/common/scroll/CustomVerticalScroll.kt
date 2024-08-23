package com.urlaunched.android.common.scroll

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.customVerticalScroll(state: ScrollState): Modifier = composed {
    if (LocalInspectionMode.current) this else verticalScroll(state = state)
}

fun Modifier.customFillMaxSize(height: Dp = 1100.dp): Modifier = composed {
    if (LocalInspectionMode.current) fillMaxSize().height(height) else fillMaxSize()
}

fun Modifier.customHeight(modifier: Modifier): Modifier = composed {
    if (LocalInspectionMode.current) fillMaxHeight() else modifier
}