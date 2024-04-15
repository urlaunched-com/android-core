package com.urlaunched.android.design.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.imageLoader

@Composable
fun UrlImage(
    modifier: Modifier = Modifier,
    model: Any?,
    placeholder: @Composable () -> Unit,
    errorPlaceholder: @Composable () -> Unit = placeholder,
    scale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null,
    alpha: Float = 1f,
    imageLoader: ImageLoader = LocalContext.current.imageLoader,
    onSuccess: () -> Unit = {}
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        contentScale = scale,
        contentDescription = contentDescription,
        colorFilter = colorFilter,
        model = model,
        alpha = alpha,
        imageLoader = imageLoader
    ) {
        val state = painter.state

        LaunchedEffect(state) {
            if (state is AsyncImagePainter.State.Success) {
                onSuccess()
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