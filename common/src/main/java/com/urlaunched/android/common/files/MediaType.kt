package com.urlaunched.android.common.files

interface MediaType {
    val mimeType: String
    val extraMimeTypes: List<String>
}