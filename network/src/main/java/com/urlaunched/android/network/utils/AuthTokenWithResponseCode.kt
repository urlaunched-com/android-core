package com.urlaunched.android.network.utils

data class AuthTokenWithResponseCode(
    val token: String,
    val code: Int?
)