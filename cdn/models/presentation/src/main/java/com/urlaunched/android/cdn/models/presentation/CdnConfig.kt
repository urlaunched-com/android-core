package com.urlaunched.android.cdn.models.presentation

data class CdnConfig(
    val publicImageCdn: String,
    val publicMediaCdn: String,
    val privateMediaCdn: String,
    val privateMediaEndpoint: String,
    val bucket: String,
    val tempBucket: String,
    val privateBucket: String
)