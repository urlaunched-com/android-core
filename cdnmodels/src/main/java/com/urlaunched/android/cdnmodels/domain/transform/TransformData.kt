package com.urlaunched.android.cdnmodels.domain.transform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransformData(
    @SerialName("bucket")
    val bucket: String,
    @SerialName("key")
    val objectKey: String,
    @SerialName("edits")
    val edits: Edits
)