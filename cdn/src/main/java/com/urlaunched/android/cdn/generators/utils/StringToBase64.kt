package com.urlaunched.android.cdn.generators.utils

import java.util.Base64

internal fun String.toBase64(): String {
    val originalByteArray = this.toByteArray()
    val encodedByteArray = Base64.getEncoder().encode(originalByteArray)
    return String(encodedByteArray)
}