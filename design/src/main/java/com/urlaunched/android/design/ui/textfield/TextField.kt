package com.urlaunched.android.design.ui.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.textfield.constants.TextFieldConstants
import com.urlaunched.android.design.ui.textfield.constants.TextFieldDimens

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String?,
    focusedBorderColor: Color = Color.Black,
    unfocusedBorderColor: Color = Color.Gray,
    errorBorderColor: Color? = Color.Red,
    errorTextColor: Color = Color.Red,
    textColor: Color = Color.Black,
    focusedTextColor: Color = textColor,
    selectionHandleColor: Color = Color.Black,
    selectionBackgroundColor: Color = Color.Black.copy(alpha = TextFieldConstants.TEXT_SELECTION_BACKGROUND_ALPHA),
    cursorBrush: Brush = SolidColor(Color.Black),
    textStyle: TextStyle = TextStyle.Default,
    labelStyle: TextStyle = TextStyle.Default,
    labelColor: Color = Color.Black,
    unfocusedLabelColor: Color = labelColor,
    errorLabelColor: Color = labelColor,
    backgroundColor: Color = Color.Transparent,
    unfocusedBackgroundColor: Color = backgroundColor,
    backgroundShape: Shape = RoundedCornerShape(TextFieldDimens.cornersRadius),
    borderWidth: Dp = TextFieldDimens.borderSize,
    borderShape: Shape = RoundedCornerShape(TextFieldDimens.cornersRadius),
    placeholderStyle: TextStyle = TextStyle.Default,
    placeholderColor: Color = Color.Gray,
    bottomLabelColor: Color = Color.Black,
    errorTextStyle: TextStyle = TextStyle.Default,
    bottomLabelTextStyle: TextStyle = TextStyle.Default,
    placeHolder: String? = null,
    bottomLabel: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    collapseLabel: Boolean = true,
    textFieldHeight: Dp? = null,
    readOnly: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    labelIcon: (@Composable () -> Unit)? = null,
    trailingIconAlwaysShown: Boolean = false,
    onValueChange: (value: String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = when {
            error != null && errorBorderColor != null -> errorBorderColor
            isFocused -> focusedBorderColor
            else -> unfocusedBorderColor
        },
        label = TextFieldConstants.LABEL_TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedTextColor by animateColorAsState(
        targetValue = when {
            error != null -> errorTextColor
            isFocused -> focusedTextColor
            else -> textColor
        },
        label = TextFieldConstants.BACKGROUND_COLOR_ANIMATION_LABEL
    )
    val animatedBackgroundColor by animateColorAsState(
        targetValue = when {
            error != null -> backgroundColor
            isFocused -> backgroundColor
            else -> unfocusedBackgroundColor
        },
        label = TextFieldConstants.TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedLabelColor by animateColorAsState(
        targetValue = when {
            error != null -> errorLabelColor
            isFocused -> labelColor
            else -> unfocusedLabelColor
        },
        label = TextFieldConstants.LABEL_TEXT_COLOR_ANIMATION_LABEL
    )

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = selectionHandleColor,
            backgroundColor = selectionBackgroundColor
        )
    ) {
        BasicTextField(
            modifier = modifier.onFocusEvent { event ->
                isFocused = event.isFocused
            },
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            cursorBrush = cursorBrush,
            textStyle = textStyle.copy(color = animatedTextColor),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                Column {
                    AnimatedVisibility(
                        visible = (value.isNotEmpty() || !collapseLabel) && !label.isNullOrEmpty(),
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
                            ) {
                                Text(
                                    text = label.orEmpty(),
                                    style = labelStyle,
                                    color = animatedLabelColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                labelIcon?.invoke()
                            }

                            Spacer(modifier = Modifier.height(Dimens.spacingSmallSpecial))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .run {
                                if (textFieldHeight != null) {
                                    height(textFieldHeight)
                                } else {
                                    this
                                }
                            }
                            .background(
                                color = animatedBackgroundColor,
                                shape = backgroundShape
                            )
                            .border(
                                width = borderWidth,
                                brush = SolidColor(borderColor),
                                shape = borderShape
                            )
                            .padding(Dimens.spacingNormal)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon.invoke()

                            Spacer(modifier = Modifier.width(Dimens.spacingSmall))
                        }

                        Box(modifier = Modifier.weight(1f).wrapContentHeight()) {
                            innerTextField()

                            if (value.isEmpty()) {
                                Text(
                                    text = placeHolder ?: label.orEmpty(),
                                    style = placeholderStyle,
                                    color = placeholderColor,
                                    maxLines = maxLines,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if ((value.isNotEmpty() || trailingIconAlwaysShown) && trailingIcon != null) {
                            Spacer(modifier = Modifier.width(Dimens.spacingSmall))

                            trailingIcon.invoke()
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingTinyHalf))

                    AnimatedVisibility(
                        visible = !error.isNullOrEmpty() || bottomLabel != null,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            text = error ?: bottomLabel.orEmpty(),
                            style = if (error != null || bottomLabel.isNullOrEmpty()) errorTextStyle else bottomLabelTextStyle,
                            color = if (error != null || bottomLabel.isNullOrEmpty()) errorTextColor else bottomLabelColor
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldPreview() {
    val text = remember { mutableStateOf("") }
    TextField(
        modifier = Modifier.padding(Dimens.spacingNormal),
        value = text.value,
        label = "Label",
        bottomLabel = "Label",
        onValueChange = { text.value = it }
    )
}

@Preview(showBackground = true)
@Composable
private fun TextFieldErrorPreview() {
    val text = remember { mutableStateOf("") }
    TextField(
        modifier = Modifier.padding(Dimens.spacingNormal),
        value = text.value,
        label = "Label",
        placeHolder = "Placeholder",
        error = "Error",
        collapseLabel = false,
        onValueChange = { text.value = it },
        labelIcon = {
            Icon(painter = rememberVectorPainter(Icons.Filled.Info), contentDescription = null)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TextFieldDisabledPreview() {
    val text = remember { mutableStateOf("") }
    TextField(
        modifier = Modifier.padding(Dimens.spacingNormal),
        value = text.value,
        label = "Label",
        enabled = false,
        onValueChange = { text.value = it }
    )
}