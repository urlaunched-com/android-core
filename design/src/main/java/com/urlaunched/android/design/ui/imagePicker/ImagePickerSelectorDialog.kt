package com.urlaunched.android.design.ui.imagePicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun ImagePickerSelectorDialog(
    modifier: Modifier = Modifier,
    config: ImagePickerDialogConfig,
    onGallerySelectClick: () -> Unit,
    onCameraSelectClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(Dimens.cornerRadiusNormal),
            colors = CardDefaults.cardColors(containerColor = config.containerColor)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.spacingBig),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = config.headerText,
                    style = config.headerTextStyle,
                    color = config.headerTextColor
                )

                Spacer(modifier = Modifier.height(Dimens.spacingBig))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.cornerRadiusNormal))
                                .clickable {
                                    onCameraSelectClick()
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            config.iconCamera()
                        }

                        Spacer(modifier = Modifier.height(Dimens.spacingNormalSpecial))

                        Text(
                            text = config.cameraText,
                            style = config.labelTextStyle,
                            color = config.labelTextColor
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.cornerRadiusNormal))
                                .clickable {
                                    onGallerySelectClick()
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            config.iconGallery()
                        }

                        Spacer(modifier = Modifier.height(Dimens.spacingNormalSpecial))

                        Text(
                            text = config.galleryText,
                            style = config.labelTextStyle,
                            color = config.labelTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spacingBig))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.cornerRadiusNormal))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(Dimens.spacingNormalSpecial),
                            text = config.onDismissText,
                            style = config.cancelTextStyle,
                            color = config.cancelTextColor
                        )
                    }
                }
            }
        }
    }
}
