package com.urlaunched.android.design.ui.accountbinding.models

import androidx.compose.ui.text.TextStyle

data class AccountBindingTextStyles(
    val sectionTitleStyle: TextStyle = TextStyle(),
    val providerStyle: TextStyle = TextStyle(),
    val accountCredentialStyle: TextStyle = TextStyle(),
    val passwordStyle: TextStyle = TextStyle(),
    val addAccountStyle: TextStyle = TextStyle(),
    val currentAccountStyle: TextStyle = TextStyle(),
    val deleteAccountStyle: TextStyle = TextStyle()
)