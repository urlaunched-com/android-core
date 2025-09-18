package com.urlaunched.android.design.ui.downloadingProgressDialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.composables.core.androidx.annotation.FloatRange
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.downloadingProgressDialog.models.ProgressBarStyle
import com.urlaunched.android.design.ui.progresstext.ProgressText

/**
 * @see ProgressText
 */
@Composable
fun DownloadingProgressDialog(
    @FloatRange(0.0, 1.0)
    progress: Float,
    downloaded: Float = progress,
    total: Float = 1f,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = DoNothing,
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    dialogContainerColor: Color = Color.White,
    dialogContainerShape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge),
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    dialogProperties: DialogProperties = DialogProperties(),
    title: (@Composable ColumnScope.() -> Unit)? = null,
    description: (@Composable ColumnScope.() -> Unit)? = null,
    progressText: (@Composable BoxScope.() -> Unit)? = null,
    supportingText: (@Composable (Float) -> Unit)? = null,
    button: (@Composable ColumnScope.() -> Unit)? = null
) {
    BaseDownloadingProgressDialog(
        progress = progress,
        total = total,
        downloaded = downloaded,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        progressBarStyle = progressBarStyle,
        dialogProperties = dialogProperties,
        dialogContainerColor = dialogContainerColor,
        dialogContainerShape = dialogContainerShape,
        contentPadding = contentPadding,
        title = title?.let { titleContent ->
            @Composable {
                titleContent()
                Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            }
        },
        description = description?.let { descriptionContent ->
            @Composable {
                descriptionContent()
                Spacer(modifier = Modifier.height(Dimens.spacingBigSpecial))
            }
        },
        progressText = progressText,
        supportingText = if (supportingText != null) {
            @Composable { value ->
                Column {
                    Spacer(modifier = Modifier.height(Dimens.spacingTiny))
                    supportingText(value)
                }
            }
        } else {
            null
        },
        button = button?.let { buttonContent ->
            @Composable {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Spacer(modifier = Modifier.height(Dimens.spacingLarge))
                    buttonContent()
                }
            }
        }
    )
}

/**
 * @see ProgressText
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDownloadingProgressDialog(
    @FloatRange(0.0, 1.0)
    progress: Float,
    downloaded: Float = progress,
    total: Float = 1f,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = DoNothing,
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    dialogContainerColor: Color = Color.White,
    dialogContainerShape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge),
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    dialogProperties: DialogProperties = DialogProperties(),
    title: (@Composable ColumnScope.() -> Unit)? = null,
    description: (@Composable ColumnScope.() -> Unit)? = null,
    progressText: (@Composable BoxScope.() -> Unit)? = null,
    supportingText: (@Composable (Float) -> Unit)? = null,
    button: (@Composable ColumnScope.() -> Unit)? = null
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        properties = dialogProperties,
        content = {
            Column(
                modifier = Modifier
                    .background(
                        color = dialogContainerColor,
                        shape = dialogContainerShape
                    )
                    .padding(contentPadding)
            ) {
                title?.invoke(this@Column)

                description?.invoke(this@Column)

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    DownloadingProgressBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(progressBarStyle.progressBarHeight),
                        progress = progress,
                        trackShape = progressBarStyle.trackShape,
                        progressShape = progressBarStyle.progressShape,
                        progressBrush = progressBarStyle.progressBrush,
                        trackBrush = progressBarStyle.trackBrush
                    )

                    progressText?.invoke(this@Box)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    supportingText?.invoke(downloaded)
                    supportingText?.invoke(total)
                }

                button?.invoke(this@Column)
            }
        }
    )
}

@Composable
private fun DownloadingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackShape: Shape,
    progressShape: Shape,
    trackBrush: Brush,
    progressBrush: Brush
) {
    Canvas(
        modifier = modifier
    ) {
        val trackOutline = trackShape.createOutline(size, layoutDirection, this)
        drawOutline(
            brush = trackBrush,
            outline = trackOutline
        )

        val progressWidth = size.width * progress.coerceIn(0f, 1f)
        val progressSize = size.copy(width = progressWidth)
        val progressOutline = progressShape.createOutline(progressSize, layoutDirection, this)

        clipPath(
            path = Path().apply { addOutline(trackOutline) }
        ) {
            drawOutline(
                brush = progressBrush,
                outline = progressOutline
            )
        }
    }
}

private val DoNothing = {}

@Preview(showBackground = true)
@Composable
private fun DownloadingProgressDialogGradientPreview() {
    val progress = remember { 0.74f }

    DownloadingProgressDialog(
        progress = progress,
        progressBarStyle = ProgressBarStyle(
            progressBrush = Brush.horizontalGradient(
                0f to Color.Yellow,
                progress to Color.Blue
            ),
            trackBrush = SolidColor(Color.LightGray)
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun BaseDownloadingProgressDialogPreview() {
    val totalValue = remember { 10f }
    val progress = remember { 0.73f }

    BaseDownloadingProgressDialog(
        progress = progress,
        total = totalValue,
        downloaded = progress * totalValue,
        title = {
            Text(
                text = "Downloading file",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        description = {
            Text(
                text = "The Adventures of Sherlock Holmes",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        progressText = {
            ProgressText(
                text = "${progress * 100f}%",
                progress = progress,
                startColor = Color.Yellow,
                endColor = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        },
        supportingText = { value ->
            Text(
                text = "$value MB",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        },
        button = {
            TextButton(
                onClick = {}
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DownloadingProgressDialogPreview() {
    val totalValue = remember { 10f }
    val progress = remember { 0.5f }

    DownloadingProgressDialog(
        progress = progress,
        total = totalValue,
        downloaded = progress * totalValue,
        title = {
            Text(
                text = "Downloading file",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        description = {
            Text(
                text = "The Adventures of Sherlock Holmes",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        progressText = {
            ProgressText(
                text = "${progress * 100f}%",
                progress = progress,
                startColor = Color.Yellow,
                endColor = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        },
        supportingText = { value ->
            Text(
                text = "$value MB",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        },
        button = {
            TextButton(
                onClick = {}
            ) {
                Text("Cancel")
            }
        }
    )
}