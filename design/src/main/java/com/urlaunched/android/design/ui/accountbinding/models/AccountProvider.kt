package com.urlaunched.android.design.ui.accountbinding.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.urlaunched.android.design.R

interface AccountProvider {
    @get:StringRes
    val nameResId: Int

    @get:DrawableRes
    val iconResId: Int?
        get() = null
}

interface PasswordBasedAccountProvider : AccountProvider

class EmailAccountProvider(override val nameResId: Int = R.string.email_provider) : PasswordBasedAccountProvider

sealed class SocialAccountProvider : AccountProvider {
    object Apple : SocialAccountProvider() {
        override val nameResId = R.string.apple_provider
        override val iconResId = R.drawable.ic_apple_icon
    }

    object Google : SocialAccountProvider() {
        override val nameResId = R.string.google_provider
        override val iconResId = R.drawable.ic_google_icon
    }

    object Facebook : SocialAccountProvider() {
        override val nameResId = R.string.facebook_provider
        override val iconResId = R.drawable.ic_facebook_icon
    }
}