package com.urlaunched.android.design.ui.textfield.hashtags

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.StateFlow

interface HashtagsDelegate {
    val uiHashtagsStage: StateFlow<HashtagsDelegateState>

    fun setCurrentHashtags(newValue: TextFieldValue)
}

data class HashtagsDelegateState(
    val currentHashtagsText: TextFieldValue
)