package com.urlaunched.android.design.ui.imagePicker

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.urlaunched.android.design.delegate.Delegate
import com.urlaunched.android.design.ui.textfield.hashtags.HashtagsDelegate
import com.urlaunched.android.design.ui.textfield.hashtags.HashtagsDelegateState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class ImagePickerDelegateImpl(
    private val coroutineDispatcher: CoroutineDispatcher
) : Delegate(coroutineDispatcher), ImagePickerDelegate {
    private val _uiState = MutableStateFlow(ImagePickerDelegateState(selectedFile = null))

    override val uiImagePickerStage: StateFlow<ImagePickerDelegateState> = _uiState
    override fun selectFile(file: File?) {
        _uiState.update { uiState ->
            uiState.copy(selectedFile = file)
        }
    }
}