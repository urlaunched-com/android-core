package com.urlaunched.android.common.coil

import coil.request.ImageResult
import coil.request.SuccessResult

fun ImageResult.onSuccess(block: () -> Unit) {
    if (this is SuccessResult) {
        block()
    }
}