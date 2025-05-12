package com.urlaunched.android.common.coil

import coil3.request.ImageResult
import coil3.request.SuccessResult

fun ImageResult.onSuccess(block: () -> Unit) {
    if (this is SuccessResult) {
        block()
    }
}