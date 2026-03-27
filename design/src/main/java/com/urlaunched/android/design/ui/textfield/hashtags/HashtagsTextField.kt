package com.urlaunched.android.design.ui.textfield.hashtags

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.textfield.LocalSelectionBackgroundColor
import com.urlaunched.android.design.ui.textfield.LocalSelectionHandleColor
import com.urlaunched.android.design.ui.textfield.LocalTextFieldBackgroundConfig
import com.urlaunched.android.design.ui.textfield.LocalTextFieldBorderConfig
import com.urlaunched.android.design.ui.textfield.LocalTextFieldInputPlaceholderTextConfig
import com.urlaunched.android.design.ui.textfield.LocalTextFieldInputTextConfig
import com.urlaunched.android.design.ui.textfield.LocalTextFieldTopLabelConfig
import com.urlaunched.android.design.ui.textfield.LocalTextFieldsSpacerConfig
import com.urlaunched.android.design.ui.textfield.constants.TextFieldConstants
import com.urlaunched.android.design.ui.textfield.models.TextFieldBackgroundConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldBorderConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldInputPlaceholderTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldInputTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldTopLabelConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldsSpacerConfig

/**
 * Use this composable together with [HashtagsDelegate] and [HashtagsDelegateImpl].
 *
 * To use [HashtagsDelegateImpl], you must manually inject or provide a [CoroutineDispatcher],
 * since the library module does not support Hilt directly.
 *
 */

@Composable
fun HashtagsTextField(
    modifier: Modifier = Modifier,
    innerFieldModifier: Modifier = Modifier,
    value: TextFieldValue = TextFieldValue(),
    label: String? = null,
    placeHolder: String?,
    error: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    borderConfig: TextFieldBorderConfig = LocalTextFieldBorderConfig.current,
    backgroundConfig: TextFieldBackgroundConfig = LocalTextFieldBackgroundConfig.current,
    inputTextConfig: TextFieldInputTextConfig = LocalTextFieldInputTextConfig.current,
    inputPlaceholderTextConfig: TextFieldInputPlaceholderTextConfig = LocalTextFieldInputPlaceholderTextConfig.current,
    topLabelConfig: TextFieldTopLabelConfig = LocalTextFieldTopLabelConfig.current,
    textFieldsSpacerConfig: TextFieldsSpacerConfig = LocalTextFieldsSpacerConfig.current,
    selectionHandleColor: Color = LocalSelectionHandleColor.current,
    selectionBackgroundColor: Color = LocalSelectionBackgroundColor.current,
    cursorBrush: Brush = SolidColor(Color.Black),
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    collapseLabel: Boolean = true,
    textFieldHeight: Dp? = null,
    innerPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    labelIcon: (@Composable () -> Unit)? = null,
    trailingIconAlwaysShown: Boolean = false,
    onValueChange: (TextFieldValue) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

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

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = selectionHandleColor,
            backgroundColor = selectionBackgroundColor
        )
    ) {
        BasicTextField(
            modifier = modifier,
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
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Column {
                    AnimatedVisibility(
                        visible = (value.text.isNotEmpty() || !collapseLabel) && !label.isNullOrEmpty(),
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
                            .clip(backgroundConfig.shape)
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
                        verticalAlignment = Alignment.CenterVertically
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

                            if (value.text.isEmpty()) {
                                Text(
                                    text = placeHolder ?: label.orEmpty(),
                                    style = inputPlaceholderTextConfig.textStyle,
                                    color = inputPlaceholderTextConfig.color,
                                    maxLines = maxLines,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if ((value.text.isNotEmpty() || trailingIconAlwaysShown) && trailingIcon != null) {
                            Spacer(modifier = Modifier.width(textFieldsSpacerConfig.trailingIconSpacer))
                            trailingIcon.invoke()
                        }
                    }

                    Spacer(modifier = Modifier.height(textFieldsSpacerConfig.errorSpacer))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HashtagsContainerPreview() {
    HashtagsTextField(
        modifier = Modifier.padding(Dimens.spacingNormal),
        value = TextFieldValue("#hasthag"),
        label = "Hashtags",
        onValueChange = {},
        placeHolder = "Enter hashtags"
    )
}

@Preview(showBackground = true)
@Composable
private fun HashtagsContainerDisabledPreview() {
    HashtagsTextField(
        modifier = Modifier.padding(Dimens.spacingNormal),
        value = TextFieldValue("#hasthag"),
        label = "Hashtags",
        enabled = false,
        onValueChange = {},
        placeHolder = "Enter hashtags"
    )
}