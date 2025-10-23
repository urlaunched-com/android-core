package com.urlaunched.android.design.ui.imagepicker.model

import androidx.annotation.IntRange

@JvmInline
value class ImagesCount(
    @IntRange(from = 1)
    val count: Int
) : Comparable<ImagesCount> {

    override fun compareTo(other: ImagesCount): Int {
        return this.count.compareTo(other.count)
    }

    companion object {
        val SINGLE_IMAGE = ImagesCount(1)
        val MULTIPLES_IMAGES_DEFAULT = ImagesCount(6)
        val UNLIMITED = ImagesCount(Int.MAX_VALUE)
    }
}