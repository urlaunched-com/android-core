package com.urlaunched.android.common.resources

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.io.InputStream

interface AppResources {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg args: Any): String
    fun openRawResource(@RawRes id: Int): InputStream
}