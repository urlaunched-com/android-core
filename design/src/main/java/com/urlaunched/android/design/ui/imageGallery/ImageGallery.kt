package com.urlaunched.android.design.ui.imageGallery

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.image.UrlImage
import com.urlaunched.android.design.ui.imageGallery.utils.detectCustomTransformGestures
import com.urlaunched.android.design.ui.imageGallery.utils.handleDoubleTap
import com.urlaunched.android.design.ui.imageGallery.utils.handleTransformGesture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ImageGalleryStyle(
    val imageToThumbnailSpacing: Dp = ImageGalleryDimens.spacingExtraLarge,
    val thumbnailItemsSpacing: Dp = ImageGalleryDimens.zero
)

@Composable
fun ImageGallery(
    modifier: Modifier = Modifier,
    images: List<Any>,
    style: ImageGalleryStyle = ImageGalleryStyle(),
    onLoadMore: () -> Unit,
    onPageChange: (pageIndex: Int) -> Unit = {},
    thumbnailItem: @Composable (index: Int, image: Any, isSelected: Boolean, onClick: () -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )
    val listState = rememberLazyListState()

    val scaleStates = remember(images) { List(images.size) { Animatable(1f) } }
    val offsetStates = remember(images) { List(images.size) { mutableStateOf(Offset.Zero) } }

    LaunchedEffect(pagerState.currentPage) {
        listState.animateScrollToItem(pagerState.currentPage)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                if (scaleStates[pagerState.currentPage].value > 1f) available else Offset.Zero
        }
    }

    Column(modifier = modifier) {
        GalleryHorizontalPager(
            pagerState,
            nestedScrollConnection,
            scaleStates,
            offsetStates,
            coroutineScope,
            images
        )

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
                .height(80.dp)
        ) {
            itemsIndexed(images) { index, image ->
                if (index == images.lastIndex) {
                    LaunchedEffect(Unit) {
                        onLoadMore()
                    }
                }

                thumbnailItem(
                    index,
                    image,
                    index == pagerState.currentPage,
                    {
                        coroutineScope.launch {
                            scaleStates[index].snapTo(1f)
                            offsetStates[index].value = Offset.Zero
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        scaleStates[pagerState.currentPage].snapTo(1f)
        offsetStates[pagerState.currentPage].value = Offset.Zero
        onPageChange(pagerState.currentPage)
    }
}

@Composable
fun ColumnScope.GalleryHorizontalPager(
    pagerState: PagerState,
    nestedScrollConnection: NestedScrollConnection,
    scaleStates: List<Animatable<Float, AnimationVector1D>>,
    offsetStates: List<MutableState<Offset>>,
    coroutineScope: CoroutineScope,
    images: List<Any>
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection)
    ) { page ->
        GalleryPage(
            page = page,
            scale = scaleStates[page],
            offset = offsetStates[page],
            coroutineScope = coroutineScope,
            image = images[page]
        )
    }
}

@Composable
private fun GalleryPage(
    page: Int,
    scale: Animatable<Float, AnimationVector1D>,
    offset: MutableState<Offset>,
    coroutineScope: CoroutineScope,
    image: Any
) {
    val containerSize = remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { containerSize.value = it }
            .pointerInput(page) {
                detectCustomTransformGestures(
                    consume = scale.value > 1f,
                    onGesture = { _, pan, zoom, _, _, changes ->
                        handleTransformGesture(
                            scale = scale,
                            offset = offset,
                            containerSize = containerSize.value,
                            pan = pan,
                            zoom = zoom,
                            coroutineScope = coroutineScope
                        )

                        if (changes.size > 1) {
                            changes.forEach {
                                if (it.positionChanged()) it.consume()
                            }
                        }
                    }
                )
            }
            .pointerInput(page) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        handleDoubleTap(
                            scale = scale,
                            offset = offset,
                            containerSize = containerSize.value,
                            tapOffset = tapOffset,
                            coroutineScope = coroutineScope
                        )
                    }
                )
            }
    ) {
        ZoomableImage(
            scale = scale,
            offset = offset,
            image = image
        )
    }
}

@Composable
private fun ZoomableImage(scale: Animatable<Float, AnimationVector1D>, offset: MutableState<Offset>, image: Any) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        UrlImage(
            model = image,
            scale = ContentScale.FillWidth,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    translationX = offset.value.x,
                    translationY = offset.value.y
                )
                .fillMaxWidth(),
            placeholder = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                )
            }
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
        onLoadMore = {},
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