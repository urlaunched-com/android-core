package com.urlaunched.android.models.presentation.image.transform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ResizeMode {
    @SerialName("cover")
    COVER,

    @SerialName("contain")
    CONTAIN,

    @SerialName("fill")
    FILL,

    @SerialName("inside")
    INSIDE,

    @SerialName("outside")
    OUTSIDE
}