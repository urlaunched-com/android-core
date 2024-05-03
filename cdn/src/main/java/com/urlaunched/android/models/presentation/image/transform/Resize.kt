package com.urlaunched.android.models.presentation.image.transform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Resize(
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("fit")
    val resizeMode: ResizeMode = ResizeMode.COVER
)