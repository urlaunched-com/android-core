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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.textfield.constants.TextFieldConstants
import com.urlaunched.android.design.ui.textfield.models.TextFieldBackgroundConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldBorderConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldBottomLabelConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldCounterConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldErrorTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldInputPlaceholderTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldInputTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldTopLabelConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldsSpacerConfig

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    innerFieldModifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    borderConfig: TextFieldBorderConfig = TextFieldBorderConfig(),
    inputTextAlignment: Alignment.Vertical = Alignment.CenterVertically,
    inputTextConfig: TextFieldInputTextConfig = TextFieldInputTextConfig(),
    errorTextConfig: TextFieldErrorTextConfig = TextFieldErrorTextConfig(),
    inputPlaceholderTextConfig: TextFieldInputPlaceholderTextConfig = TextFieldInputPlaceholderTextConfig(),
    topLabelConfig: TextFieldTopLabelConfig = TextFieldTopLabelConfig(),
    bottomLabelConfig: TextFieldBottomLabelConfig = TextFieldBottomLabelConfig(),
    counterConfig: TextFieldCounterConfig = TextFieldCounterConfig(),
    backgroundConfig: TextFieldBackgroundConfig = TextFieldBackgroundConfig(),
    textFieldsSpacerConfig: TextFieldsSpacerConfig = TextFieldsSpacerConfig(),
    selectionHandleColor: Color = Color.Black,
    selectionBackgroundColor: Color = Color.Black.copy(alpha = TextFieldConstants.TEXT_SELECTION_BACKGROUND_ALPHA),
    cursorBrush: Brush = SolidColor(Color.Black),
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
    innerPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    labelIcon: (@Composable () -> Unit)? = null,
    trailingIconAlwaysShown: Boolean = false,
    maxSymbols: Int? = null,
    counterFormat: String? = null,
    onValueChange: (value: String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = when {
            error != null && borderConfig.errorColor != null -> borderConfig.errorColor
            isFocused -> borderConfig.focusedColor
            else -> borderConfig.unfocusedColor
        },
        label = TextFieldConstants.LABEL_TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedTextColor by animateColorAsState(
        targetValue = when {
            error != null && inputTextConfig.errorColor != null -> inputTextConfig.errorColor
            isFocused -> inputTextConfig.focusedColor
            else -> inputTextConfig.unfocusedColor
        },
        label = TextFieldConstants.BACKGROUND_COLOR_ANIMATION_LABEL
    )
    val animatedBackgroundColor by animateColorAsState(
        targetValue = when {
            error != null -> backgroundConfig.focusedColor
            isFocused -> backgroundConfig.focusedColor
            else -> backgroundConfig.unfocusedColor
        },
        label = TextFieldConstants.TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedLabelColor by animateColorAsState(
        targetValue = when {
            error != null && topLabelConfig.errorColor != null -> topLabelConfig.errorColor
            isFocused -> topLabelConfig.focusedColor
            else -> topLabelConfig.unfocusedColor
        },
        label = TextFieldConstants.LABEL_TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedErrorTextColor by animateColorAsState(
        targetValue = when {
            isFocused -> errorTextConfig.focusedColor
            else -> errorTextConfig.unfocusedColor
        },
        label = TextFieldConstants.ERROR_TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedBottomLabelTextColor by animateColorAsState(
        targetValue = when {
            isFocused -> bottomLabelConfig.focusedColor
            else -> bottomLabelConfig.unfocusedColor
        },
        label = TextFieldConstants.BOTTOM_LABEL_TEXT_COLOR_ANIMATION_LABEL
    )
    val animatedCounterTextColor by animateColorAsState(
        targetValue = when {
            isFocused -> counterConfig.focusedColor
            else -> counterConfig.unfocusedColor
        },
        label = TextFieldConstants.COUNTER_TEXT_COLOR_ANIMATION_LABEL
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
            textStyle = inputTextConfig.textStyle.copy(color = animatedTextColor),
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
                                horizontalArrangement = Arrangement.spacedBy(textFieldsSpacerConfig.labelIconSpacer)
                            ) {
                                Text(
                                    text = label.orEmpty(),
                                    style = topLabelConfig.textStyle,
                                    color = animatedLabelColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                labelIcon?.invoke()
                            }

                            Spacer(modifier = Modifier.height(textFieldsSpacerConfig.labelSpacer))
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
                            .then(innerFieldModifier)
                            .background(
                                color = animatedBackgroundColor,
                                shape = backgroundConfig.shape
                            )
                            .border(
                                width = borderConfig.width,
                                brush = SolidColor(borderColor),
                                shape = borderConfig.shape
                            )
                            .padding(
                                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                            )
                            .fillMaxWidth(),
                        verticalAlignment = inputTextAlignment
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon.invoke()

                            Spacer(modifier = Modifier.width(textFieldsSpacerConfig.leadingIconSpacer))
                        }

                        Box(
                            modifier = Modifier
                                .padding(
                                    top = innerPadding.calculateTopPadding(),
                                    bottom = innerPadding.calculateBottomPadding()
                                )
                                .weight(1f)
                                .wrapContentHeight()
                        ) {
                            innerTextField()

                            if (value.isEmpty()) {
                                Text(
                                    text = placeHolder ?: label.orEmpty(),
                                    style = inputPlaceholderTextConfig.textStyle,
                                    color = inputPlaceholderTextConfig.color,
                                    maxLines = maxLines,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if ((value.isNotEmpty() || trailingIconAlwaysShown) && trailingIcon != null) {
                            Spacer(modifier = Modifier.width(textFieldsSpacerConfig.trailingIconSpacer))

                            trailingIcon.invoke()
                        }
                    }

                    Spacer(modifier = Modifier.height(textFieldsSpacerConfig.errorSpacer))

                    AnimatedVisibility(
                        visible = !error.isNullOrEmpty() || bottomLabel != null || maxSymbols != null,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Row {
                            Text(
                                text = error ?: bottomLabel.orEmpty(),
                                style = if (error != null || bottomLabel.isNullOrEmpty()) errorTextConfig.textStyle else bottomLabelConfig.textStyle,
                                color = if (error != null || bottomLabel.isNullOrEmpty()) animatedErrorTextColor else animatedBottomLabelTextColor
                            )

                            maxSymbols?.let {
                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = counterFormat ?: "${value.length}/$maxSymbols",
                                    style = counterConfig.textStyle,
                                    color = if (error != null) animatedErrorTextColor else animatedCounterTextColor
                                )
                            }
                        }
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