package com.urlaunched.android.common.string

// Special method for working with bundle arguments
fun String?.orNull(): String? = if (this == "null") {
    null
} else {
    this
}