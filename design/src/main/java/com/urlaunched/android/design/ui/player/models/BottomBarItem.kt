package com.urlaunched.android.design.ui.player.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class BottomBarItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val modifier: Modifier = Modifier,
    val enabled: Boolean = true
)