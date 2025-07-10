package com.urlaunched.android.design.ui.textfield.hashtags

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HashtagsDelegateImpl(
    private val coroutineDispatcher: CoroutineDispatcher
) : Delegate(coroutineDispatcher), HashtagsDelegate {
    private val _uiState = MutableStateFlow(HashtagsDelegateState(currentHashtagsText = TextFieldValue()))

    override val uiHashtagsStage: StateFlow<HashtagsDelegateState> = _uiState

    override fun setCurrentHashtags(newValue: TextFieldValue) {
        val rawText = newValue.text
        val selection = newValue.selection
        val previousText = _uiState.value.currentHashtagsText.text

        val filteredText = rawText.filter { it.isLetter() || it.isDigit() || it == ' ' || it == '#' }

        val updatedText = buildString {
            if (previousText.isEmpty() && filteredText.isNotEmpty()) {
                append("#")
                append(filteredText)
            } else if (filteredText.isEmpty()) {
                append("")
            } else if (filteredText.lastOrNull() == ' ' && previousText.length <= filteredText.length) {
                if (previousText.lastOrNull() != '#') {
                    append(filteredText.dropLast(1))
                    append(" #")
                } else {
                    append(previousText)
                }
            } else {
                append(filteredText)
            }
        }

        val cursorPos = when {
            previousText.isEmpty() && filteredText.isNotEmpty() -> {
                updatedText.length
            }

            updatedText.length > filteredText.length -> {
                updatedText.length
            }

            updatedText.length < filteredText.length -> {
                selection.start - (filteredText.length - updatedText.length)
            }

            filteredText.lastOrNull() == ' ' && previousText.lastOrNull() == '#' -> {
                updatedText.length
            }

            else -> {
                selection.start
            }
        }.coerceIn(0, updatedText.length)

        _uiState.update {
            it.copy(currentHashtagsText = TextFieldValue(updatedText, TextRange(cursorPos)))
        }
    }
}