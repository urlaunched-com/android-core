package com.urlaunched.android.design.ui.otpContainer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.otpContainer.models.OtpCellColors
import com.urlaunched.android.design.ui.otpContainer.models.OtpCellStyle
import com.urlaunched.android.design.ui.otpContainer.models.OtpCellsArrangement

private const val DEFAULT_OTP_LENGTH = 6

/**
 * A composable OTP container input field where each character is shown in an individual box.
 *
 * @param modifier Modifier applied to the root of the text field.
 * @param otpLength The total number of characters expected in the OTP code. Default is 6.
 * @param hasError If true, the OTP boxes will show an error state (e.g., red border color).
 * @param otpText The current OTP input as a plain [String]. The cursor will automatically be placed at the end.
 * @param onOtpTextChange Callback invoked when the OTP value changes.
 * @param cellsArrangement Defines how the OTP boxes are arranged horizontally. Use [OtpCellsArrangement.SpaceBetween]
 * for evenly distributed boxes, or [OtpCellsArrangement.Spaced] to specify exact spacing between them.
 * @param cellsStyle Configuration of the visual properties for each OTP cell, such as size, shape, text style, and border widths.
 * @param cellsColors Configuration of the color states (focused, error, empty, filled) and backgrounds for OTP cells.
 */

@Composable
fun OtpTextField(
    modifier: Modifier,
    otpLength: Int = DEFAULT_OTP_LENGTH,
    hasError: Boolean,
    otpText: String,
    onOtpTextChange: (text: String) -> Unit,
    cellsArrangement: OtpCellsArrangement = OtpCellsArrangement.Spaced(Dimens.spacingSmall),
    cellsStyle: OtpCellStyle = OtpCellStyle(),
    cellsColors: OtpCellColors = OtpCellColors()
) {
    val horizontalArrangement = when (cellsArrangement) {
        is OtpCellsArrangement.SpaceBetween -> Arrangement.SpaceBetween
        is OtpCellsArrangement.Spaced -> Arrangement.spacedBy(cellsArrangement.spacing)
    }

    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpLength) {
                onOtpTextChange(it.text)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                modifier = Modifier,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    OtpCellView(
                        index = index,
                        text = otpText,
                        isError = hasError,
                        isFocused = otpText.length == index,
                        style = cellsStyle,
                        colors = cellsColors
                    )
                }
            }
        }
    )
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
            .border(borderWidth, currentBorderColor, style.shape)
            .clip(style.shape)
            .padding(borderWidth)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            modifier = Modifier,
            style = style.textStyle,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldPreview() {
    var textFieldValue by remember {
        mutableStateOf("")
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.TopCenter
    ) {
        OtpTextField(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpText = textFieldValue,
            onOtpTextChange = { textFieldValue = it },
            cellsArrangement = OtpCellsArrangement.SpaceBetween,
            hasError = false
        )
    }
}