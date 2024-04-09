package com.urlaunched.android.cdn.models.domain.transaform

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