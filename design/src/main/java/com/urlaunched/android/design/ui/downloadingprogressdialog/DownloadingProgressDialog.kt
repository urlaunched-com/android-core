package com.urlaunched.android.design.ui.downloadingprogressdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.composables.core.androidx.annotation.FloatRange
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.downloadingprogressdialog.models.ProgressBarStyle
import com.urlaunched.android.design.ui.progresstext.ProgressText

/**
 * @see ProgressText
 */
@Composable
fun DownloadingProgressDialog(
    modifier: Modifier = Modifier,
    @FloatRange(0.0, 1.0)
    progress: Float,
    downloaded: Float = progress,
    total: Float = 1f,
    onDismissRequest: () -> Unit = {},
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    dialogContainerColor: Color = Color.White,
    dialogContainerShape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge),
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    dialogProperties: DialogProperties = DialogProperties(),
    title: (@Composable ColumnScope.() -> Unit)? = null,
    description: (@Composable ColumnScope.() -> Unit)? = null,
    progressText: (@Composable BoxScope.(Float) -> Unit)? = null,
    supportingText: (@Composable RowScope.(Float) -> Unit)? = null,
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
        supportingText = supportingText,
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
    modifier: Modifier = Modifier,
    @FloatRange(0.0, 1.0)
    progress: Float,
    downloaded: Float = progress,
    total: Float = 1f,
    onDismissRequest: () -> Unit = {},
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    dialogContainerColor: Color = Color.White,
    dialogContainerShape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge),
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    dialogProperties: DialogProperties = DialogProperties(),
    title: (@Composable ColumnScope.() -> Unit)? = null,
    description: (@Composable ColumnScope.() -> Unit)? = null,
    progressText: (@Composable BoxScope.(Float) -> Unit)? = null,
    supportingText: (@Composable RowScope.(Float) -> Unit)? = null,
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

                DownloadingProgressBar(
                    progress = progress,
                    downloaded = downloaded,
                    total = total,
                    modifier = Modifier.fillMaxWidth(),
                    progressBarHeight = progressBarStyle.progressBarHeight,
                    trackShape = progressBarStyle.trackShape,
                    progressShape = progressBarStyle.progressShape,
                    progressBrush = progressBarStyle.progressBrush,
                    trackBrush = progressBarStyle.trackBrush,
                    progressText = progressText,
                    supportingText = supportingText
                )

                button?.invoke(this@Column)
            }
        }
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