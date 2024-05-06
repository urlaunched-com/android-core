package com.urlaunched.android.design.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntSize
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.imageLoader
import coil.request.SuccessResult
import com.urlaunched.android.cdn.models.presentation.image.CdnImagePresentationModel
import com.urlaunched.android.cdn.models.presentation.image.transform.Edits
import com.urlaunched.android.cdn.models.presentation.image.transform.Resize
import com.urlaunched.android.cdn.models.presentation.image.transform.ResizeMode
import kotlin.math.roundToInt

@Composable
fun UrlImage(
    modifier: Modifier = Modifier,
    model: Any?,
    placeholder: @Composable () -> Unit = {},
    errorPlaceholder: @Composable () -> Unit = placeholder,
    scale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null,
    alpha: Float = 1f,
    imageLoader: ImageLoader = LocalContext.current.imageLoader,
    cdnScaleFactor: Float = 1f,
    onSuccess: (result: SuccessResult) -> Unit = {}
) {
    var imageSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val link by remember(model, imageSize, cdnScaleFactor) {
        derivedStateOf {
            when (model) {
                is CdnImagePresentationModel.Public -> {
                    if (imageSize != IntSize.Zero) {
                        model.resizedLink(
                            edits = Edits(
                                resize = getResizeMode(scale, imageSize, cdnScaleFactor.coerceAtLeast(0.1f))
                            )
                        )
                    } else {
                        null
                    }
                }

                else -> model
            }
        }
    }

    SubcomposeAsyncImage(
        modifier = modifier
            .onSizeChanged { size ->
                imageSize = size
            },
        contentScale = scale,
        contentDescription = contentDescription,
        colorFilter = colorFilter,
        model = link,
        alpha = alpha,
        imageLoader = imageLoader
    ) {
        val state = painter.state

        LaunchedEffect(state) {
            if (state is AsyncImagePainter.State.Success) {
                onSuccess(state.result)
            }
        }

        when {
            LocalInspectionMode.current -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4CAF50))
                )
            }

            state is AsyncImagePainter.State.Error -> {
                errorPlaceholder()
            }

            state is AsyncImagePainter.State.Loading -> {
                placeholder()
            }

            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}

private fun getResizeMode(scale: ContentScale, imageSize: IntSize, cdnScaleFactor: Float) = Resize(
    width = if (scale != ContentScale.FillHeight) (imageSize.width * cdnScaleFactor).roundToInt() else null,
    height = if (scale != ContentScale.FillWidth) (imageSize.height * cdnScaleFactor).roundToInt() else null,
    resizeMode = when (scale) {
        ContentScale.Inside -> {
            ResizeMode.INSIDE
        }

        ContentScale.Fit -> {
            ResizeMode.INSIDE
        }

        ContentScale.FillBounds -> {
            ResizeMode.FILL
        }

        ContentScale.None -> {
            null
        }

        else -> {
            ResizeMode.COVER
        }
    }
)