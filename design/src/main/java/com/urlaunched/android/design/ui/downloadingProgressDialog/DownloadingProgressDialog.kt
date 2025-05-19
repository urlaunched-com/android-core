package com.urlaunched.android.design.ui.downloadingProgressDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.downloadingProgressDialog.models.DialogStyle
import com.urlaunched.android.design.ui.downloadingProgressDialog.models.ProgressBarColors
import com.urlaunched.android.design.ui.downloadingProgressDialog.models.ProgressBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadingProgressDialog(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    progressBarColors: ProgressBarColors = ProgressBarColors(),
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    dialogStyle: DialogStyle = DialogStyle(),
    progressText: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    description: @Composable () -> Unit = {},
    definitionText: @Composable () -> Unit = {},
    button: @Composable () -> Unit = {}
) {
    BasicAlertDialog(
        modifier = modifier
            .padding(Dimens.spacingNormal)
            .clip(RoundedCornerShape(progressBarStyle.dialogCornerRadius))
            .background(progressBarColors.backgroundDialogColor),
        onDismissRequest = {
            // Do nothing
        },
        properties = DialogProperties(usePlatformDefaultWidth = LocalInspectionMode.current),
        content = {
            Box {
                Column(modifier = Modifier.padding(dialogStyle.contentPadding)) {
                    description()

                    title()

                    BoxWithConstraints(
                        modifier = Modifier
                            .background(
                                progressBarColors.progressBackgroundColor,
                                RoundedCornerShape(progressBarStyle.progressCornerRadius)
                            )
                            .height(progressBarStyle.progressBarHeight),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        val currentWidth =
                            (maxWidth - Dimens.spacingLarge) * progress + Dimens.spacingLarge

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(progressBarStyle.progressBoxCornerRadius))
                                .defaultMinSize(minWidth = Dimens.spacingLarge)
                                .height(progressBarStyle.progressBarHeight)
                                .background(
                                    if (progressBarColors.progressColors.size > 1) {
                                        Brush.horizontalGradient(progressBarColors.progressColors)
                                    } else {
                                        SolidColor(progressBarColors.progressColors.first())
                                    }
                                )
                                .width(currentWidth)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = currentWidth - 32.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            progressText()
                        }
                    }

                    definitionText()

                    button()
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DownloadingProgressDialogPreview() {
    DownloadingProgressDialog()
}