package com.urlaunched.android.cdn.generators

import com.urlaunched.android.cdn.generators.utils.bucket
import com.urlaunched.android.cdn.generators.utils.objectKey
import com.urlaunched.android.cdn.generators.utils.toBase64
import com.urlaunched.android.cdn.models.CdnConfig
import com.urlaunched.android.cdn.models.domain.links.MediaLink
import com.urlaunched.android.cdn.models.domain.media.MediaDomainModel
import com.urlaunched.android.cdn.models.domain.transaform.Edits
import com.urlaunched.android.cdn.models.domain.transaform.TransformData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MediaCDNGenerator {
    fun generateImageLink(media: MediaDomainModel, cdnConfig: CdnConfig): MediaLink {
        val rawLink = media.mediaRawLink
        return when {
            rawLink.startsWith(cdnConfig.privateBucket) -> generatePrivateLink(media = media, cdnConfig = cdnConfig)
            else -> generatePublicLink(media = media, cdnConfig = cdnConfig)
        }
    }

    fun generateNotImageLink(media: MediaDomainModel, cdnConfig: CdnConfig): MediaLink {
        val rawLink = media.mediaRawLink
        return when {
            rawLink.startsWith(cdnConfig.privateBucket) -> generatePrivateLink(media = media, cdnConfig = cdnConfig)
            else -> generatePublicLink(media = media, cdnConfig = cdnConfig)
        }
    }

    private fun generatePublicLink(media: MediaDomainModel, cdnConfig: CdnConfig): MediaLink.Public {
        val id = media.id
        val objectKey = media.objectKey
        val link = "${cdnConfig.publicMediaCdn}/$objectKey"
        return MediaLink.Public(id, link, media.sizeKb)
    }

    private fun generatePrivateLink(media: MediaDomainModel, cdnConfig: CdnConfig): MediaLink.Private {
        val id = media.id
        val link = "${cdnConfig.privateMediaEndpoint}/$id"
        return MediaLink.Private(id, link, media.sizeKb)
    }

    fun generateBitmapLink(media: MediaDomainModel, edits: Edits, cdnConfig: CdnConfig): MediaLink.Public {
        val id = media.id
        val transformData = TransformData(
            bucket = media.bucket,
            objectKey = media.objectKey,
            edits = edits
        )
        val rawJson = Json.encodeToString(transformData)

        val link = "${cdnConfig.publicImageCdn}/${rawJson.toBase64()}"
        return MediaLink.Public(id, link, media.sizeKb)
    }
}