package com.urlaunched.android.design.ui.downloadingprogressdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.composables.core.androidx.annotation.FloatRange
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.downloadingprogressdialog.models.ProgressBarStyle
import com.urlaunched.android.design.ui.downloadingprogressdialog.models.ProgressTextStyle

@Composable
fun DownloadingProgressDialog(
    modifier: Modifier = Modifier,
    @FloatRange(0.0, 1.0)
    progress: Float,
    progressText: String? = null,
    progressTextStyle: ProgressTextStyle = ProgressTextStyle(),
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    dialogContainerColor: Color = Color.White,
    onDismissRequest: () -> Unit = {},
    dialogContainerShape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge),
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    dialogProperties: DialogProperties = DialogProperties(),
    title: (@Composable ColumnScope.() -> Unit)? = null,
    description: (@Composable ColumnScope.() -> Unit)? = null,
    supportingText: (@Composable BoxScope.() -> Unit)? = null,
    bottomContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    BaseDownloadingProgressDialog(
        progress = progress,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        progressBarStyle = progressBarStyle,
        dialogProperties = dialogProperties,
        dialogContainerColor = dialogContainerColor,
        dialogContainerShape = dialogContainerShape,
        contentPadding = contentPadding,
        progressText = progressText,
        progressTextStyle = progressTextStyle,
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
        supportingText = supportingText,
        bottomContent = bottomContent?.let { bottomContent ->
            @Composable {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Spacer(modifier = Modifier.height(Dimens.spacingLarge))

                    bottomContent()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDownloadingProgressDialog(
    modifier: Modifier = Modifier,
    @FloatRange(0.0, 1.0)
    progress: Float,
    progressText: String? = null,
    progressTextStyle: ProgressTextStyle = ProgressTextStyle(),
    progressBarStyle: ProgressBarStyle = ProgressBarStyle(),
    onDismissRequest: () -> Unit = {},
    dialogContainerColor: Color = Color.White,
    dialogContainerShape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge),
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    dialogProperties: DialogProperties = DialogProperties(),
    title: (@Composable ColumnScope.() -> Unit)? = null,
    description: (@Composable ColumnScope.() -> Unit)? = null,
    supportingText: (@Composable BoxScope.() -> Unit)? = null,
    bottomContent: (@Composable ColumnScope.() -> Unit)? = null
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
                    modifier = Modifier.fillMaxWidth(),
                    progressBarHeight = progressBarStyle.progressBarHeight,
                    trackShape = progressBarStyle.trackShape,
                    progressShape = progressBarStyle.progressShape,
                    progressBrush = progressBarStyle.progressBrush,
                    trackBrush = progressBarStyle.trackBrush,
                    progressText = progressText,
                    progressTextStyle = progressTextStyle,
                    supportingText = supportingText
                )

                bottomContent?.invoke(this@Column)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BaseDownloadingProgressDialogPreview() {
    val progress = remember { 0.73f }

    BaseDownloadingProgressDialog(
        progress = progress,
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
        progressText = "${progress * 100f}%",
        supportingText = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(progress * 100, 100).forEach { value ->
                    Text(
                        text = "$value MB",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        bottomContent = {
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
    val progress = remember { 0.5f }

    DownloadingProgressDialog(
        progress = progress,
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
        progressText = "${progress * 100f}%",
        supportingText = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(progress * 100, 100).forEach { value ->
                    Text(
                        text = "$value MB",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        bottomContent = {
            TextButton(
                onClick = {}
            ) {
                Text("Cancel")
            }
        }
    )
}