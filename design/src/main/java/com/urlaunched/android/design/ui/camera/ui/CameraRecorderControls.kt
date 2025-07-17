package com.urlaunched.android.design.ui.camera.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.Layout
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.camera.model.CameraModeTypePresentationModel
import com.urlaunched.android.design.ui.camera.model.RecordingStatePresentationModel

@Composable
internal fun CameraRecorderControls(
    modifier: Modifier = Modifier,
    recordingState: RecordingStatePresentationModel,
    cameraModeType: CameraModeTypePresentationModel,
    isGalleryEnabled: Boolean,
    onVideoCameraClick: () -> Unit,
    onPhotoCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    selectedCameraOption: @Composable () -> Unit,
    unselectedCameraOption: @Composable (onClick: () -> Unit) -> Unit,
    galleryButton: @Composable (onClick: () -> Unit) -> Unit,
    cameraToggleButton: @Composable (onClick: () -> Unit) -> Unit,
    recordMediaButton: @Composable  BoxScope.() -> Unit
) {
    Column(modifier = modifier) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            contentAlignment = Alignment.Center,
            targetState = recordingState is RecordingStatePresentationModel.Recording
        ) { isRecording ->
            if (isRecording) {
                selectedCameraOption()
            } else {
                AnimatedContent(targetState = cameraModeType) { type ->
                    CameraOptionLayout(
                        firstOption = { selectedCameraOption() },
                        secondOption = {
                            unselectedCameraOption(
                                if (type == CameraModeTypePresentationModel.PHOTO) {
                                    onVideoCameraClick
                                } else {
                                    onPhotoCameraClick
                                }
                            )
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(Dimens.spacingSmall))

        Box(modifier = Modifier.height(IntrinsicSize.Max)) {
            recordMediaButton()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = recordingState !is RecordingStatePresentationModel.Recording && isGalleryEnabled) {
                    galleryButton(onGalleryClick)
                }

                AnimatedVisibility(visible = recordingState !is RecordingStatePresentationModel.Recording) {
                    cameraToggleButton(onToggleCameraClick)
                }
            }
        }
    }
}

@Composable
private fun CameraOptionLayout(
    modifier: Modifier = Modifier,
    firstOption: @Composable () -> Unit,
    secondOption: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            firstOption()
            secondOption()
        }
    ) { measurables, constraints ->
        if (measurables.size < 2) {
            return@Layout layout(0, 0) { }
        }

        val firstButton = measurables[0].measure(constraints)
        val secondButton = measurables[1].measure(constraints)

        val width = firstButton.width + secondButton.width * 2 + Dimens.spacingBig.roundToPx() * 2
        val height = maxOf(secondButton.height, firstButton.height)

        layout(width, height) {
            val firstButtonX = secondButton.width + Dimens.spacingBig.roundToPx()

            firstButton.placeRelative(firstButtonX, 0)
            secondButton.placeRelative(
                x = firstButtonX + firstButton.width + Dimens.spacingBig.roundToPx(),
                0
            )
        }
    }
}

