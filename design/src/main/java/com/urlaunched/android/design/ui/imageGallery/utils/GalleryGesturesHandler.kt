package com.urlaunched.android.design.ui.imageGallery.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

internal fun handleDoubleTap(
    scale: Animatable<Float, AnimationVector1D>,
    offset: MutableState<Offset>,
    containerSize: IntSize,
    tapOffset: Offset,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        val targetScale = if (scale.value > 1f) 1f else 3f

        if (targetScale == 1f) {
            launch { scale.animateTo(1f) }
            launch {
                Animatable(offset.value, Offset.VectorConverter)
                    .animateTo(Offset.Zero) {
                        offset.value = value
                    }
            }
        } else {
            val containerCenter = Offset(
                x = containerSize.width / 2f,
                y = containerSize.height / 2f
            )

            val newOffset = (containerCenter - tapOffset) * (targetScale - 1f)

            launch { scale.animateTo(targetScale) }

            launch {
                Animatable(offset.value, Offset.VectorConverter)
                    .animateTo(clampOffset(newOffset, targetScale, containerSize)) {
                        offset.value = value
                    }
            }
        }
    }
}

internal fun handleTransformGesture(
    scale: Animatable<Float, AnimationVector1D>,
    offset: MutableState<Offset>,
    containerSize: IntSize,
    pan: Offset,
    zoom: Float,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        val newScale = (scale.value * zoom).coerceIn(1f, 5f)
        scale.snapTo(newScale)

        if (newScale > 1f) {
            val newOffset = offset.value + pan
            offset.value = clampOffset(newOffset, newScale, containerSize)
        } else {
            offset.value = Offset.Zero
        }
    }
}

internal fun clampOffset(offset: Offset, scale: Float, containerSize: IntSize): Offset {
    val scaledWidth = containerSize.width * scale
    val scaledHeight = containerSize.height * scale

    val maxX = (scaledWidth - containerSize.width) / 2
    val maxY = (scaledHeight - containerSize.height) / 2

    return Offset(
        x = offset.x.coerceIn(-maxX, maxX),
        y = offset.y.coerceIn(-maxY, maxY)
    )
}

internal suspend fun PointerInputScope.detectCustomTransformGestures(
    panZoomLock: Boolean = false,
    consume: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = {},
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) -> Unit,
    onGestureEnd: (PointerInputChange) -> Unit = {}
) {
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        val down: PointerInputChange = awaitFirstDown(
            requireUnconsumed = false,
            pass = pass
        )

        onGestureStart(down)

        var pointer = down
        var pointerId = down.id

        do {
            val event = awaitPointerEvent(pass = pass)

            val canceled = event.changes.any { it.isConsumed }

            if (!canceled) {
                val pointerInputChange =
                    event.changes.firstOrNull { it.id == pointerId }
                        ?: event.changes.first()

                pointerId = pointerInputChange.id
                pointer = pointerInputChange

                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion =
                        abs(rotation * kotlin.math.PI.toFloat() * centroidSize / 180f)
                    val panMotion = pan.getDistance()

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop ||
                        panMotion > touchSlop
                    ) {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                    if (effectiveRotation != 0f ||
                        zoomChange != 1f ||
                        panChange != Offset.Zero
                    ) {
                        onGesture(
                            centroid,
                            panChange,
                            zoomChange,
                            effectiveRotation,
                            pointer,
                            event.changes
                        )
                    }

                    if (consume) {
                        event.changes.forEach {
                            if (it.positionChanged()) {
                                it.consume()
                            }
                        }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })
        onGestureEnd(pointer)
    }
}