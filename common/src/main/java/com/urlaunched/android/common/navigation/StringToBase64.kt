package com.urlaunched.android.common.navigation

import java.util.Base64

fun String.toUrlBase64(): String {
    val originalByteArray = this.toByteArray()
    val encodedByteArray = Base64.getUrlEncoder().encode(originalByteArray)
    return String(encodedByteArray)
}

fun String.fromUrlBase64(): String {
    val originalByteArray = this.toByteArray()
    val decodedByteArray = Base64.getUrlDecoder().decode(originalByteArray)
    return String(decodedByteArray)
}