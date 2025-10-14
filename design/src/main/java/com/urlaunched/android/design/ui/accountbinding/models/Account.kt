package com.urlaunched.android.design.ui.accountbinding.models

interface Account {
    val credential: String?
    val isCurrent: Boolean
}

data class DefaultAccount(
    override val credential: String?,
    override val isCurrent: Boolean
) : Account