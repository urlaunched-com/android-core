package com.urlaunched.android.design.ui.otpcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.otpcontainer.constants.OTPContainerDefaults
import com.urlaunched.android.design.ui.shadow.shadow

private const val DEFAULT_OTP_LENGTH = 6

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
                            char = otpText.getOrNull(index)?.toString().orEmpty(),
                            isFocused = otpText.length == index
                        )
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldPreview() {
    var textFieldValue by remember { mutableStateOf("12") }

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
        ) {
            Text(
                text = char,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(40.dp, 48.dp)
                    .shadow(
                        color = Color.Black,
                        shadowBlurRadius = 6.dp,
                        offset = DpOffset(x = 0.dp, y = 2.dp),
                        alpha = 0.20f
                    )
                    .clip(RoundedCornerShape(Dimens.cornerRadiusSmall))
                    .background(Color.White)
                    .run {
                        when {
                            isFocused -> this.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
                            )

                            char.isNotBlank() -> this.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(Dimens.cornerRadiusSmall)
                            )

                            else -> this
                        }
                    }
                    .padding(vertical = Dimens.spacingNormal)
            )
        }
    }
}