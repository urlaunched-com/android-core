package com.urlaunched.android.design.ui.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.textfield.constants.TextFieldConstants
import com.urlaunched.android.design.ui.textfield.models.TextFieldBackgroundConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldBorderConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldBottomLabelConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldErrorTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldInputPlaceholderTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldInputTextConfig
import com.urlaunched.android.design.ui.textfield.models.TextFieldTopLabelConfig

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    borderConfig: TextFieldBorderConfig = TextFieldBorderConfig(),
    inputTextConfig: TextFieldInputTextConfig = TextFieldInputTextConfig(),
    errorTextConfig: TextFieldErrorTextConfig = TextFieldErrorTextConfig(),
    inputPlaceholderTextConfig: TextFieldInputPlaceholderTextConfig = TextFieldInputPlaceholderTextConfig(),
    topLabelConfig: TextFieldTopLabelConfig = TextFieldTopLabelConfig(),
    bottomLabelConfig: TextFieldBottomLabelConfig = TextFieldBottomLabelConfig(),
    backgroundConfig: TextFieldBackgroundConfig = TextFieldBackgroundConfig(),
    selectionHandleColor: Color = Color.Black,
    selectionBackgroundColor: Color = Color.Black.copy(alpha = TextFieldConstants.TEXT_SELECTION_BACKGROUND_ALPHA),
    cursorBrush: Brush = SolidColor(Color.Black),
    placeHolder: String? = null,
    bottomLabel: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textFieldHeight: Dp? = null,
    readOnly: Boolean = false,
    innerPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    leadingIcon: (@Composable () -> Unit)? = null,
    labelIcon: (@Composable () -> Unit)? = null,
    onValueChange: (value: String) -> Unit,
    passwordVisibleIcon: @Composable () -> Unit,
    passwordHiddenIcon: @Composable () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = value,
        label = label,
        placeHolder = placeHolder,
        collapseLabel = false,
        onValueChange = onValueChange,
        singleLine = true,
        trailingIconAlwaysShown = true,
        error = error,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        borderConfig = borderConfig,
        errorTextConfig = errorTextConfig,
        inputTextConfig = inputTextConfig,
        inputPlaceholderTextConfig = inputPlaceholderTextConfig,
        backgroundConfig = backgroundConfig,
        topLabelConfig = topLabelConfig,
        bottomLabel = bottomLabel,
        bottomLabelConfig = bottomLabelConfig,
        textFieldHeight = textFieldHeight,
        readOnly = readOnly,
        innerPadding = innerPadding,
        leadingIcon = leadingIcon,
        selectionHandleColor = selectionHandleColor,
        selectionBackgroundColor = selectionBackgroundColor,
        cursorBrush = cursorBrush,
        labelIcon = labelIcon,
        enabled = enabled,
        keyboardActions = keyboardActions,
        trailingIcon = {
            Box(
                modifier = Modifier.toggleable(
                    value = isPasswordVisible,
                    onValueChange = { value -> isPasswordVisible = value },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    role = Role.Switch
                )
            ) {
                if (!isPasswordVisible) {
                    passwordHiddenIcon()
                } else {
                    passwordVisibleIcon()
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordTextFieldPreview() {
    val text = remember { mutableStateOf("") }
    PasswordTextField(
        modifier = Modifier.padding(Dimens.spacingNormal),
        value = text.value,
        label = "Label",
        placeHolder = "Placeholder",
        onValueChange = { text.value = it },
        passwordVisibleIcon = {
            Icon(painter = rememberVectorPainter(Icons.Outlined.Info), contentDescription = null)
        },
        passwordHiddenIcon = {
            Icon(painter = rememberVectorPainter(Icons.Outlined.Clear), contentDescription = null)
        }
    )
}