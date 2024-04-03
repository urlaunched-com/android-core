package com.urlaunched.android.design.ui.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.textfield.constants.TextFieldConstants
import com.urlaunched.android.design.ui.textfield.constants.TextFieldDimens

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    focusedBorderColor: Color = Color.Black,
    unfocusedBorderColor: Color = Color.Gray,
    errorBorderColor: Color = Color.Red,
    errorTextColor: Color = Color.Red,
    textColor: Color = Color.Black,
    selectionHandleColor: Color = Color.Black,
    selectionBackgroundColor: Color = Color.Black.copy(alpha = TextFieldConstants.TEXT_SELECTION_BACKGROUND_ALPHA),
    cursorBrush: Brush = SolidColor(Color.Black),
    textStyle: TextStyle = TextStyle.Default,
    labelStyle: TextStyle = TextStyle.Default,
    labelColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
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
    keyboardActions: KeyboardActions = KeyboardActions.Default,
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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        errorBorderColor = errorBorderColor,
        errorTextColor = errorTextColor,
        textColor = textColor,
        selectionHandleColor = selectionHandleColor,
        selectionBackgroundColor = selectionBackgroundColor,
        cursorBrush = cursorBrush,
        textStyle = textStyle,
        labelIcon = labelIcon,
        labelStyle = labelStyle,
        labelColor = labelColor,
        backgroundColor = backgroundColor,
        backgroundShape = backgroundShape,
        borderShape = borderShape,
        bottomLabel = bottomLabel,
        borderWidth = borderWidth,
        placeholderColor = placeholderColor,
        placeholderStyle = placeholderStyle,
        bottomLabelTextStyle = bottomLabelTextStyle,
        bottomLabelColor = bottomLabelColor,
        errorTextStyle = errorTextStyle,
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