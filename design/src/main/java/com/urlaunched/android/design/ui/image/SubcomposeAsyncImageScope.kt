package com.urlaunched.android.design.ui.image

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.request.SuccessResult
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.image.UrlImage as UrlCoreImage

@Composable
fun SubcomposeAsyncImageScope(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(Dimens.zeroDp),
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f,
    placeholder: @Composable (() -> Unit) = {},
    errorPlaceholder: @Composable (() -> Unit) = {},
    onSuccess: (result: SuccessResult) -> Unit = {}
) {
    UrlCoreImage(
        modifier = modifier.clip(shape),
        model = model,
        placeholder = placeholder,
        scale = contentScale,
        colorFilter = colorFilter,
        contentDescription = contentDescription,
        alpha = alpha,
        onSuccess = onSuccess,
        errorPlaceholder = errorPlaceholder
    )
}