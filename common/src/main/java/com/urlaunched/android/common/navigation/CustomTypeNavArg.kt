package com.urlaunched.android.common.navigation

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
    This methods map Serializable objects to nav arg parameter and vise versa
    It use toUrlBase64() to prevent objects like {"link", "https://google.com"} break navigation.
    It will encode this object to "eyJsaW5rIiwgImh0dHBzOi8vZ29vZ2xlLmNvbSJ9" string that don't have
    any special symbols like ':', '/',
 */

inline fun <reified T> T.toNavArgString(): String = Json.encodeToString(this).toUrlBase64()

inline fun <reified T> String.fromNavArgString(): T = Json.decodeFromString(this.fromUrlBase64())