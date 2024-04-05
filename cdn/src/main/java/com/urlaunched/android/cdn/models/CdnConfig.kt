package com.urlaunched.android.cdn.models

data class CdnConfig(
    val publicImageCdn: String,
    val publicMediaCdn: String,
    val privateMediaCdn: String,
    val privateMediaEndpoint: String,
    val bucket: String,
    val tempBucket: String,
    val privateBucket: String
)