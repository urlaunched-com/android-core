package com.urlaunched.android.models.presentation.image.transform

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