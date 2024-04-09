package com.urlaunched.android.ble.utils

import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response

internal fun <T : Any> Result<T>.toResponse() = fold(
    onSuccess = { data ->
        Response.Success(data)
    },
    onFailure = { cause ->
        Response.Error(ErrorData(code = null, message = cause.message))
    }
)