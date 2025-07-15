package com.urlaunched.android.design.ui.accountbinding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun CurrentAccountOrDeleteContainer(
    modifier: Modifier = Modifier,
    hasAccount: Boolean,
    isCurrentAccount: Boolean,
    currentAccountContainer: @Composable () -> Unit,
    deleteAccountContainer: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasAccount) {
            if (isCurrentAccount) {
                currentAccountContainer()
            } else {
                deleteAccountContainer()
            }
        }
    }
}