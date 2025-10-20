package com.urlaunched.android.design.ui.otpcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.modifiers.ifNotNull
import com.urlaunched.android.design.ui.otpcontainer.constants.OTPContainerDefaults
import com.urlaunched.android.design.ui.otpcontainer.models.OTPCellColors
import com.urlaunched.android.design.ui.otpcontainer.models.OTPCellStyle
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle
import com.urlaunched.android.design.ui.shadow.shadow

private const val DEFAULT_OTP_LENGTH = 6

@Composable
fun OTPContainerWithError(
    modifier: Modifier = Modifier,
    otpText: String,
    onOtpTextChange: (text: String) -> Unit,
    error: String? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    errorTextStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.error),
    errorContentPadding: PaddingValues = PaddingValues(top = Dimens.spacingTiny),
    otpLength: Int = DEFAULT_OTP_LENGTH,
    cellsArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    cellStyle: OTPCellStyle = OTPCellStyle(),
    cellColors: OTPCellColors = OTPCellColors(),
    keyboardOptions: KeyboardOptions = OTPContainerDefaults.DefaultKeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(
        modifier = modifier
    ) {
        OTPContainer(
            modifier = Modifier.fillMaxWidth(),
            otpText = otpText,
            onOtpTextChange = onOtpTextChange,
            otpLength = otpLength,
            cellsArrangement = cellsArrangement,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cellContent = {
                OTPCell(
                    isError = error != null,
                    style = cellStyle,
                    colors = cellColors,
                    textStyle = textStyle
                )
            }
        )

        if (!error.isNullOrEmpty()) {
            Text(
                text = error,
                style = errorTextStyle,
                modifier = Modifier.padding(errorContentPadding)
            )
        }
    }
}

@Composable
fun OTPContainer(
    modifier: Modifier = Modifier,
    otpText: String,
    onOtpTextChange: (text: String) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current,
    otpLength: Int = DEFAULT_OTP_LENGTH,
    cellsArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    isError: Boolean = false,
    cellStyle: OTPCellStyle = OTPCellStyle(),
    cellColors: OTPCellColors = OTPCellColors(),
    keyboardOptions: KeyboardOptions = OTPContainerDefaults.DefaultKeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OTPContainer(
        modifier = modifier,
        otpText = otpText,
        onOtpTextChange = onOtpTextChange,
        otpLength = otpLength,
        cellsArrangement = cellsArrangement,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cellContent = {
            OTPCell(
                isError = isError,
                style = cellStyle,
                colors = cellColors,
                textStyle = textStyle
            )
        }
    )
}

@Composable
fun OTPContainer(
    modifier: Modifier = Modifier,
    otpText: String,
    onOtpTextChange: (text: String) -> Unit,
    otpLength: Int = DEFAULT_OTP_LENGTH,
    cellsArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    keyboardOptions: KeyboardOptions = OTPContainerDefaults.DefaultKeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    cellContent: @Composable OTPCellScope.() -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpLength) {
                onOtpTextChange(it.text)
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = {
            Row(
                horizontalArrangement = cellsArrangement,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    cellContent(
                        OTPCellScope(
                            char = otpText.getOrNull(index),
                            isFocused = otpText.length == index
                        )
                    )
                }
            }
        }
    )
}

@Composable
private fun OTPCellScope.OTPCell(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    style: OTPCellStyle = OTPCellStyle(),
    colors: OTPCellColors = OTPCellColors()
) {
    val char = char?.toString().orEmpty()

    val currentBorderColor = when {
        isError -> colors.errorBorderColor
        isFocused -> colors.focusedBorderColor
        char.isNotBlank() -> colors.borderColor
        else -> colors.emptyBorderColor
    }

    val borderWidth = if (isFocused) {
        style.focusedBorderWidth
    } else {
        style.unfocusedBorderWidth
    }

    val backgroundColor = when {
        isFocused -> colors.focusedBackgroundColor
        char.isNotBlank() -> colors.backgroundColor
        else -> colors.emptyBackgroundColor
    }

    Box(
        modifier = modifier
            .size(width = style.width, height = style.height)
            .ifNotNull(style.shadowStyle) { Modifier.shadow(it) }
            .clip(style.shape)
            .background(backgroundColor)
            .border(borderWidth, currentBorderColor, style.shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = textStyle,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun OTPContainerPreview() {
    var textFieldValue by remember { mutableStateOf("1234") }

    Box(
        modifier = Modifier.background(Color.LightGray)
    ) {
        OTPContainer(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            onOtpTextChange = { textFieldValue = it },
            cellsArrangement = Arrangement.SpaceBetween
        )
    }
}

@Preview
@Composable
private fun OTPContainerShadowPreview() {
    var textFieldValue by remember { mutableStateOf("12") }

    Box(
        modifier = Modifier.background(Color.LightGray)
    ) {
        OTPContainer(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            cellStyle = OTPCellStyle(
                shadowStyle = ShadowStyle(
                    color = Color.Black,
                    blurRadius = 6.dp,
                    offset = DpOffset(x = 0.dp, y = 2.dp),
                    alpha = 0.20f
                )
            ),
            onOtpTextChange = { textFieldValue = it },
            cellsArrangement = Arrangement.SpaceBetween
        )
    }
}

@Preview
@Composable
private fun OTPContainerErrorPreview() {
    var textFieldValue by remember { mutableStateOf("123456") }

    Box(
        modifier = Modifier.background(Color.LightGray)
    ) {
        OTPContainerWithError(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            error = "Something went wrong, try another one",
            onOtpTextChange = { textFieldValue = it },
            cellsArrangement = Arrangement.SpaceBetween
        )
    }
}