package com.urlaunched.android.design.ui.stories

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.image.FixedSubcomposeAsyncImage

@Composable
fun PromoStoryItem(
    modifier: Modifier,
    storyImage: Any,
    maxHeight: Dp,
    placeholder: @Composable () -> Unit,
    errorPlaceholder: @Composable () -> Unit
) {
    FixedSubcomposeAsyncImage(
        imageSize = IntSize(maxHeight.value.toInt(), maxHeight.value.toInt()),
        modifier = modifier,
        model = storyImage,
        contentDescription = null,
        scale = ContentScale.Crop,
        placeholder = placeholder,
        errorPlaceholder = errorPlaceholder
    )
}

@Preview
@Composable
private fun PromoStoryItemPreview() {
    val maxHeight = 80.dp
    PromoStoryItem(
        modifier = Modifier
            .clip(CircleShape)
            .heightIn(max = maxHeight)
            .aspectRatio(1f)
            .border(
                width = Dimens.borderWidth,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(4.dp)
            .clip(CircleShape),
        storyImage = "https://plus.unsplash.com/premium_photo-1664474619075-644dd191935f?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8aW1hZ2V8ZW58MHx8MHx8fDA%3D",
        maxHeight = maxHeight,
        errorPlaceholder = {},
        placeholder = {}
    )
}