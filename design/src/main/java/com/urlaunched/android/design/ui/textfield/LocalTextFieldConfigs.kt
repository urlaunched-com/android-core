package com.urlaunched.android.design.ui.textfield

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

@Stable
private val DefaultTextFieldInputTextConfig = TextFieldInputTextConfig()
val LocalTextFieldInputTextConfig = compositionLocalOf { DefaultTextFieldInputTextConfig }

@Stable
private val DefaultTextFieldBorderConfig = TextFieldBorderConfig()
val LocalTextFieldBorderConfig = compositionLocalOf { DefaultTextFieldBorderConfig }

@Stable
private val DefaultTextFieldErrorTextConfig = TextFieldErrorTextConfig()
val LocalTextFieldErrorTextConfig = compositionLocalOf { DefaultTextFieldErrorTextConfig }

@Stable
private val DefaultTextFieldInputPlaceholderTextConfig = TextFieldInputPlaceholderTextConfig()
val LocalTextFieldInputPlaceholderTextConfig = compositionLocalOf { DefaultTextFieldInputPlaceholderTextConfig }

@Stable
private val DefaultTextFieldTopLabelConfig = TextFieldTopLabelConfig()
val LocalTextFieldTopLabelConfig = compositionLocalOf { DefaultTextFieldTopLabelConfig }

@Stable
private val DefaultTextFieldBackgroundConfig = TextFieldBackgroundConfig()
val LocalTextFieldBackgroundConfig = compositionLocalOf { DefaultTextFieldBackgroundConfig }

@Stable
private val DefaultSelectionHandleColor = Color.Black
val LocalSelectionHandleColor = compositionLocalOf { DefaultSelectionHandleColor }

@Stable
private val DefaultSelectionBackgroundColor = Color.Black.copy(alpha = TextFieldConstants.TEXT_SELECTION_BACKGROUND_ALPHA)
val LocalSelectionBackgroundColor = compositionLocalOf { DefaultSelectionBackgroundColor }

@Stable
private val DefaultTextFieldBottomLabelConfig = TextFieldBottomLabelConfig()
val LocalTextFieldBottomLabelConfig = compositionLocalOf { DefaultTextFieldBottomLabelConfig }

@Stable
private val DefaultTextFieldCounterConfig = TextFieldCounterConfig()
val LocalTextFieldCounterConfig = compositionLocalOf { DefaultTextFieldCounterConfig }

@Stable
private val DefaultTextFieldsSpacerConfig = TextFieldsSpacerConfig()
val LocalTextFieldsSpacerConfig = compositionLocalOf { DefaultTextFieldsSpacerConfig }

@Stable
private val DefaultCursorBrush = SolidColor(Color.Black)
val LocalCursorBrush = compositionLocalOf { DefaultCursorBrush }
