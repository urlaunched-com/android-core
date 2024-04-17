package com.urlaunched.android.cdnmodels.domain.transform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Resize(
    @SerialName("width")
    val width: Int? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("fit")
    val resizeMode: ResizeMode
)