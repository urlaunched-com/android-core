package com.urlaunched.android.design.ui.backhandler

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun DisableBackButton() {
    BackHandler {
        // Do nothing
    }
}