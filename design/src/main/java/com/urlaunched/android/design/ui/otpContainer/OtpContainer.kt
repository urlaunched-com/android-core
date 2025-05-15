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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urlaunched.android.design.ui.otpContainer.models.OtpCellStyle

/**
 * A composable OTP container input field where each character is shown in an individual box.
 *
 * @param modifier Modifier applied to the root of the text field.
 * @param otpLength The total number of characters expected in the OTP code. Default is 6.
 * @param hasError If true, the OTP boxes will show an error state (e.g., red border).
 * @param otpTextFieldValue The current text input as a [TextFieldValue], including cursor position.
 * @param onOtpTextFieldValueChange Callback invoked when the OTP value changes.
 * @param elementsSpacing The spacing between individual character boxes. Ignored if [useSpaceBetween] is true.
 * @param useSpaceBetween If true, distributes the boxes evenly across the available width using [Arrangement.SpaceBetween].
 * @param cellsStyle Visual configuration for the individual character boxes, defined by [OtpCellStyle].
 */

@Composable
fun OtpTextField(
    modifier: Modifier,
    otpLength: Int = 6,
    hasError: Boolean,
    otpTextFieldValue: TextFieldValue,
    onOtpTextFieldValueChange: (TextFieldValue) -> Unit,
    elementsSpacing: Dp = 8.dp,
    useSpaceBetween: Boolean = false,
    cellsStyle: OtpCellStyle = OtpCellStyle(),
) {

    BasicTextField(
        modifier = modifier,
        value = otpTextFieldValue,
        onValueChange = {
            if (it.text.length <= otpLength) {
                onOtpTextFieldValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                modifier = Modifier,
                horizontalArrangement = if (useSpaceBetween) Arrangement.SpaceBetween else Arrangement.spacedBy(
                    elementsSpacing
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    OtpCellView(
                        index = index,
                        text = otpTextFieldValue.text,
                        isError = hasError,
                        isFocused = otpTextFieldValue.selection.start == index,
                        style = cellsStyle
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
) {
    val char = text.getOrNull(index)?.toString().orEmpty()

    val currentBorderColor = when {
        isError -> style.errorBorderColor
        isFocused -> style.focusedBorderColor
        char.isNotBlank() -> style.filledBorderColor
        else -> style.emptyBorderColor
    }

    val borderWidth = if (isFocused) {
        style.focusedBorderWidth
    } else {
        style.unfocusedBorderWidth
    }

    val backgroundColor = if(char.isNotBlank()){
        style.backgroundColor
    }else{
        style.emptyBackgroundColor
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
        mutableStateOf(TextFieldValue("45", selection = TextRange(2)))
    }
    Box(Modifier
        .fillMaxSize()
        .background(Color.Gray), contentAlignment = Alignment.TopCenter) {
        OtpTextField(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            otpTextFieldValue = textFieldValue,
            onOtpTextFieldValueChange = { textFieldValue = it },
            useSpaceBetween = true,
            hasError = false,
            cellsStyle = OtpCellStyle().copy(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                focusedBorderColor = Color(0xFF1e7e9c),
                filledBorderColor = Color(0xFFD8D9DF),
                textStyle = TextStyle(
                    fontWeight = FontWeight(700),
                    fontSize = 22.sp, color = Color.Black
                ),
                focusedBorderWidth = 1.5.dp,
                unfocusedBorderWidth = 1.5.dp

            )
        )
    }
}


