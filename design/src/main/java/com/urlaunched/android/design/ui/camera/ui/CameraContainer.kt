package com.urlaunched.android.design.ui.camera.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.camera.model.CameraModeTypePresentationModel
import com.urlaunched.android.design.ui.camera.model.RecordingStatePresentationModel

data class CameraContainerColors(
    val backgroundColor: Color = Color.Black,
    val surfaceColor: Color = Color.White,
    val closeButtonBackground: Color = Color.Black,
    val closeButtonIconTint: Color = Color.White,
    val flashButtonBackground: Color = Color.Black,
    val flashButtonIconTint: Color = Color.White,
    val progressBrush: Brush = SolidColor(Color.Black),
    val selectedOptionBackground: Color = Color.White,
    val selectedOptionTextColor: Color = Color.Black,
    val unselectedOptionBackground: Color = Color.DarkGray,
    val unselectedOptionTextColor: Color = Color.White,
    val controlButtonBackground: Color = Color.Black,
    val controlButtonBorderColor: Color = Color.DarkGray,
    val controlIconTint: Color = Color.White,
)

data class CameraContainerConfig(
    val colors: CameraContainerColors = CameraContainerColors(),
    val surfaceCornerRadius: Dp = Dimens.cornerRadiusNormalSpecial,
    val surfacePaddingBottom: Dp = Dimens.spacingLarge,
    val flashIconSize: Dp = Dimens.iconSizeNormalSpecial,
    val flashIconInnerPadding: Dp = CameraRecorderDimens.flashIconPadding,
    val topButtonsSpacing: Dp = Dimens.spacingNormalSpecial,
    val controlPaddingHorizontal: Dp = Dimens.spacingNormalSpecial,
    val controlPaddingBottom: Dp = Dimens.spacingNormalSpecial,
)

@Composable
fun CameraContainer(
    modifier: Modifier = Modifier,
    config: CameraContainerConfig = CameraContainerConfig(),
    recordingState: RecordingStatePresentationModel,
    cameraMode: CameraModeTypePresentationModel,
    isFlashEnabled: Boolean,
    isGalleryEnabled: Boolean,
    onShutterClick: () -> Unit,
    onCameraPreviewViewAvailable: (androidx.camera.view.PreviewView) -> Unit,
    onVideoCameraClick: () -> Unit,
    onPhotoCameraClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    onToggleFlashClick: () -> Unit,
    onGalleryClick: () -> Unit,
    goBack: () -> Unit,
    selectedCameraOption: @Composable () -> Unit,
    unselectedCameraOption: @Composable (onClick: () -> Unit) -> Unit,
    galleryButton: @Composable (onClick: () -> Unit) -> Unit,
    cameraToggleButton: @Composable (onClick: () -> Unit) -> Unit,
    closeButton: @Composable () -> Unit = {},
    flashOnButton: @Composable () -> Unit = {},
    flashOffButton: @Composable () -> Unit = {},
    recordMediaButton : @Composable BoxScope.() -> Unit = {
        RecordMediaButton(
            modifier = Modifier
                .size(CameraRecorderDimens.recordMediaButtonSize)
                .align(Alignment.Center),
            isRecording = recordingState is RecordingStatePresentationModel.Recording,
            progress = (recordingState as? RecordingStatePresentationModel.Recording)?.progress ?: 0f,
            progressBrush = config.colors.progressBrush,
            onClick = onShutterClick
        )
    }
) {
    Box(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .background(config.colors.backgroundColor)
            .statusBarsPadding()
            .padding(bottom = config.surfacePaddingBottom)
            .background(
                color = config.colors.surfaceColor,
                shape = RoundedCornerShape(config.surfaceCornerRadius)
            )
            .clip(RoundedCornerShape(config.surfaceCornerRadius))
    ) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onCameraPreviewViewAvailable = onCameraPreviewViewAvailable
        )

        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = recordingState !is RecordingStatePresentationModel.Recording
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.topButtonsSpacing),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.clickable(onClick = goBack)) {
                    closeButton()
                }

                AnimatedContent(isFlashEnabled) { enabled ->
                    Box(modifier = Modifier.clickable(onClick = onToggleFlashClick)) {
                        if (enabled) {
                            flashOnButton()
                        } else {
                            flashOffButton()
                        }
                    }
                }
            }
        }

        CameraRecorderControls(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = config.controlPaddingHorizontal)
                .padding(bottom = config.controlPaddingBottom),
            cameraModeType = cameraMode,
            recordingState = recordingState,
            isGalleryEnabled = isGalleryEnabled,
            onVideoCameraClick = onVideoCameraClick,
            onToggleCameraClick = onToggleCameraClick,
            onPhotoCameraClick = onPhotoCameraClick,
            onGalleryClick = onGalleryClick,
            selectedCameraOption = selectedCameraOption,
            unselectedCameraOption = unselectedCameraOption,
            galleryButton = galleryButton,
            cameraToggleButton = cameraToggleButton,
            recordMediaButton = recordMediaButton
        )
    }
}

