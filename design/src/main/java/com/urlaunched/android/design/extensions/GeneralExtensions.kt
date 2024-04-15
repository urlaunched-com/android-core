package com.urlaunched.android.design.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx(): Int {
    val density = LocalDensity.current
    return (this * density.density).value.toInt()
}