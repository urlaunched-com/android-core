package com.urlaunched.android.cdn.generators.utils

import com.urlaunched.android.cdnmodels.domain.media.MediaDomainModel

internal val MediaDomainModel.bucket: String
    get() = mediaRawLink
        .substringAfter("://")
        .substringBefore('.')

internal val MediaDomainModel.objectKey: String
    get() = mediaRawLink
        .substringAfter("://")
        .substringAfter('/')