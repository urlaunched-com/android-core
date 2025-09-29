package com.urlaunched.android.design.ui.otptextfield

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.otptextfield.constants.OtpTextFieldDefaults
import com.urlaunched.android.design.ui.otptextfield.models.OtpCellColors
import com.urlaunched.android.design.ui.otptextfield.models.OtpCellStyle

private const val DEFAULT_OTP_LENGTH = 6

@Composable
fun OtpTextField(
    modifier: Modifier,
    otpText: String,
    onOtpTextChange: (text: String) -> Unit,
    error: String? = null,
    otpLength: Int = DEFAULT_OTP_LENGTH,
    cellsArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.spacingSmall),
    keyboardOptions: KeyboardOptions = OtpTextFieldDefaults.DefaultKeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    cellsStyle: OtpCellStyle = OtpCellStyle(),
    cellsColors: OtpCellColors = OtpCellColors()
) {
    Column(modifier) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
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
                        OtpCellView(
                            index = index,
                            text = otpText,
                            isError = error != null,
                            isFocused = otpText.length == index,
                            style = cellsStyle,
                            colors = cellsColors
                        )
                    }
                }
            }
        )

        if (!error.isNullOrEmpty()) {
            Spacer(Modifier.height(cellsStyle.errorTextTopPadding))

            Text(
                text = error,
                style = cellsStyle.errorTextStyle,
                color = cellsColors.errorBorderColor
            )
        }
    }
}

@Composable
private fun OtpCellView(
    index: Int,
    text: String,
    isError: Boolean,
    isFocused: Boolean,
    style: OtpCellStyle,
    colors: OtpCellColors
) {
    val char = text.getOrNull(index)?.toString().orEmpty()

    val currentBorderColor = when {
        isError -> colors.errorBorderColor
        isFocused -> colors.focusedBorderColor
        char.isNotBlank() -> colors.filledBorderColor
        else -> colors.emptyBorderColor
    }

    val borderWidth = if (isFocused) {
        style.focusedBorderWidth
    } else {
        style.unfocusedBorderWidth
    }

    val backgroundColor = if (char.isNotBlank() || isFocused) {
        colors.backgroundColor
    } else {
        colors.emptyBackgroundColor
    }

    Box(
        Modifier
            .size(width = style.width, height = style.height)
            .clip(style.shape)
            .background(backgroundColor)
            .border(borderWidth, currentBorderColor, style.shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = style.textStyle,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldPreview() {
    var textFieldValue by remember { mutableStateOf("12") }

    Box(
        modifier = Modifier.background(Color.Black)
    ) {
        OtpTextField(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            onOtpTextChange = { textFieldValue = it },
            cellsArrangement = Arrangement.SpaceBetween
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldErrorPreview() {
    var textFieldValue by remember { mutableStateOf("123") }

    Box(
        modifier = Modifier.background(Color.Black)
    ) {
        OtpTextField(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            onOtpTextChange = { textFieldValue = it },
            error = "Otp is incorrect, try another one",
            cellsArrangement = Arrangement.SpaceBetween
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldErrorWithoutMessagePreview() {
    var textFieldValue by remember { mutableStateOf("123456") }

    Box(
        modifier = Modifier.background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        OtpTextField(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            onOtpTextChange = { textFieldValue = it },
            error = "",
            cellsArrangement = Arrangement.SpaceBetween
        )
    }
}