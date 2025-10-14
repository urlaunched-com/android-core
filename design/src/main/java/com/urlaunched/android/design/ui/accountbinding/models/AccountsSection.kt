package com.urlaunched.android.design.ui.accountbinding.models

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class AccountsSection(
    val title: String,
    val accountProviders: List<AccountProvider>,
    val trailingContent: (@Composable RowScope.() -> Unit)? = null
)