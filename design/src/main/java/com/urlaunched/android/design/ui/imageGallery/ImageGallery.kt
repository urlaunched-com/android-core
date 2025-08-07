package com.urlaunched.android.design.ui.imageGallery

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.image.UrlImage
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableState
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

private const val IMAGE_DOUBLE_TAP_ZOOM_FACTOR = 4f
private const val IMAGE_ZOOM_FACTOR = 20f
private val DEFAULT_THUMBNAIL_ROW_HEIGHT: Dp = 80.dp

data class ImageGalleryStyle(
    val imageToThumbnailSpacing: Dp = ImageGalleryDimens.spacingExtraLarge,
    val thumbnailItemsSpacing: Dp = ImageGalleryDimens.zero
)

@Composable
fun ImageGallery(
    modifier: Modifier = Modifier,
    images: List<Any>,
    style: ImageGalleryStyle = ImageGalleryStyle(),
    onPageChange: (pageIndex: Int) -> Unit = {},
    onLastIndex: (() -> Unit)? = null,
    zoomFactor: Float = IMAGE_ZOOM_FACTOR,
    doubleTapZoomFactor: Float = IMAGE_DOUBLE_TAP_ZOOM_FACTOR,
    contentScale: ContentScale = ContentScale.Fit,
    thumbnailItem: @Composable ((index: Int, image: Any, isSelected: Boolean, onClick: () -> Unit) -> Unit)? = null,
    thumbnailRowHeight: Dp = DEFAULT_THUMBNAIL_ROW_HEIGHT
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )
    val listState = rememberLazyListState()

    LaunchedEffect(pagerState.currentPage) {
        listState.animateScrollToItem(pagerState.currentPage)
    }

    val nestedScrollConnection = rememberNestedScrollInteropConnection()

    Column(modifier = modifier) {
        GalleryHorizontalPager(
            pagerState = pagerState,
            images = images,
            nestedScrollConnection = nestedScrollConnection,
            zoomFactor = zoomFactor,
            doubleTapZoomFactor = doubleTapZoomFactor,
            contentScale = contentScale
        )

        thumbnailItem?.let {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(style.imageToThumbnailSpacing)
            )

            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(style.thumbnailItemsSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(thumbnailRowHeight)
            ) {
                itemsIndexed(images) { index, image ->
                    onLastIndex?.let {
                        if (index == images.lastIndex) {
                            LaunchedEffect(Unit) {
                                onLastIndex()
                            }
                        }
                    }

                    thumbnailItem(
                        index,
                        image,
                        index == pagerState.currentPage,
                        {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onPageChange(pagerState.currentPage)
    }
}

@Composable
fun ColumnScope.GalleryHorizontalPager(
    images: List<Any>,
    pagerState: PagerState,
    nestedScrollConnection: NestedScrollConnection,
    zoomFactor: Float = IMAGE_ZOOM_FACTOR,
    doubleTapZoomFactor: Float = IMAGE_DOUBLE_TAP_ZOOM_FACTOR,
    contentScale: ContentScale = ContentScale.Fit
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection)
    ) { page ->
        val zoomableState = rememberZoomableState(
            zoomSpec = ZoomSpec(zoomFactor)
        )

        LaunchedEffect(pagerState.currentPage) {
            zoomableState.resetZoom()
        }

        GalleryPage(
            image = images[page],
            zoomableState = zoomableState,
            doubleTapZoomFactor = doubleTapZoomFactor,
            contentScale = contentScale
        )
    }
}

@Composable
private fun GalleryPage(
    image: Any,
    zoomableState: ZoomableState,
    doubleTapZoomFactor: Float = IMAGE_DOUBLE_TAP_ZOOM_FACTOR,
    contentScale: ContentScale = ContentScale.Fit
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50))
        )
    } else {
        ZoomableAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = image,
            state = rememberZoomableImageState(zoomableState),
            onDoubleClick = DoubleClickToZoomListener.cycle(doubleTapZoomFactor),
            contentDescription = null,
            contentScale = contentScale
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageGalleryCustomThumbnailPreview() {
    val sampleImages = listOf(
        "https://img.freepik.com/free-photo/top-view-table-full-food_23-2149209253.jpg?semt=ais_hybrid&w=740",
        "https://cdn.pixabay.com/photo/2018/08/04/11/30/draw-3583548_1280.png",
        "https://www.wearegecko.co.uk/media/50316/mountain-3.jpg",
        "https://cdn.prod.website-files.com/62d84e447b4f9e7263d31e94/6399a4d27711a5ad2c9bf5cd_ben-sweet-2LowviVHZ-E-unsplash-1.jpeg",
        "https://i0.wp.com/plopdo.com/wp-content/uploads/2021/11/feature-pic.jpg?fit=537%2C322&ssl=1",
        "https://cdn.pixabay.com/photo/2018/08/04/11/30/draw-3583548_1280.png",
        "https://www.wearegecko.co.uk/media/50316/mountain-3.jpg"
    )

    ImageGallery(
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxSize(),
        images = sampleImages,
        onLastIndex = {},
        onPageChange = { Log.d("IMAGE_CHANGED", it.toString()) },
        style = ImageGalleryStyle(
            imageToThumbnailSpacing = 50.dp
        ),
        thumbnailItem = { index, image, isSelected, onClick ->
            Box(
                modifier = Modifier
                    .clickable { onClick() }
                    .then(
                        if (isSelected) {
                            Modifier
                                .border(
                                    width = ImageGalleryDimens.textFieldBorderWidth,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(ImageGalleryDimens.cornerRadiusSmall)
                                )
                        } else {
                            Modifier
                        }
                    )
                    .size(ImageGalleryDimens.defaultThumbnailSize)
            ) {
                UrlImage(
                    model = image,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.DarkGray),
                    placeholder = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Gray)
                        )
                    }
                )
            }
        }
    )
}