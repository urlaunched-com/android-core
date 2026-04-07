package com.urlaunched.android.design.ui.stories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.urlaunched.android.design.ui.image.UrlImage
import com.urlaunched.android.design.ui.stories.constants.StoryConstants
import com.urlaunched.android.design.ui.stories.constants.StoryDimens

@Composable
internal fun StoryItem(
    modifier: Modifier = Modifier,
    storyImageModel: Any?,
    maxHeight: Dp,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    border: BorderStroke? = null,
    backgroundColor: Color = Color.Transparent,
    borderPadding: PaddingValues = PaddingValues(StoryDimens.borderPadding),
    onClick: () -> Unit,
    placeholder: @Composable () -> Unit = {},
    errorPlaceholder: @Composable () -> Unit = placeholder
) {
    UrlImage(
        fixedImageSize = DpSize(maxHeight, maxHeight),
        modifier = modifier
            .clip(shape)
            .heightIn(max = maxHeight)
            .aspectRatio(StoryConstants.STORY_IMAGE_ASPECT_RATIO)
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .run {
                if (border != null) {
                    this
                        .border(border, shape)
                        .padding(borderPadding)
                        .clip(shape)
                } else {
                    this
                }
            },
        model = storyImageModel,
        contentDescription = contentDescription,
        scale = ContentScale.Crop,
        placeholder = placeholder,
        errorPlaceholder = errorPlaceholder
    )
}

@Preview
@Composable
private fun PromoStoryItemPreview() {
    StoryItem(
        storyImageModel = null,
        maxHeight = StoryDimens.maxHeight,
        border = BorderStroke(width = StoryDimens.borderWidth, color = Color.LightGray),
        onClick = {}
    )
}