package com.urlaunched.android.cdn.generators.utils

import com.urlaunched.android.cdn.models.domain.media.MediaDomainModel

internal val MediaDomainModel.bucket: String
    get() = mediaRawLink
        .substringAfter("://")
        .substringBefore('.')

internal val MediaDomainModel.objectKey: String
    get() = mediaRawLink
        .substringAfter("://")
        .substringAfter('/')