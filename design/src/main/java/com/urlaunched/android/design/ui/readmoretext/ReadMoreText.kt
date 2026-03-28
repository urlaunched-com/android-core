package com.urlaunched.android.design.ui.readmoretext

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens

private const val READ_MORE_TAG = "read_more"
private const val READ_LESS_TAG = "read_less"
private const val ELLIPSIS_WITH_SPACE = "… "
private const val ELLIPSIS = "…"
private const val DEFAULT_MAX_LINES = 2

sealed class TextPosition {
    data object Inline : TextPosition()
    data class NewLine(val paddingTop: Dp = Dimens.spacingTiny) : TextPosition()
}

@Composable
fun ReadMoreText(
    modifier: Modifier = Modifier,
    text: String,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    style: TextStyle,
    onTextLayout: (textLayoutResult: TextLayoutResult) -> Unit = {},
    softWrap: Boolean = true,
    readMoreText: String,
    readMoreMaxLines: Int = DEFAULT_MAX_LINES,
    readMoreStyle: SpanStyle,
    readMoreTextPosition: TextPosition = TextPosition.NewLine(),
    readLessText: String,
    readLessStyle: SpanStyle = readMoreStyle,
    readLessTextPosition: TextPosition = TextPosition.NewLine(),
    maxLinesExpanded: Int? = null
) {
    ReadMoreText(
        text = AnnotatedString(text),
        expanded = expanded,
        modifier = modifier,
        onExpandedChange = onExpandedChange,
        style = style,
        onTextLayout = onTextLayout,
        softWrap = softWrap,
        readMoreText = readMoreText,
        readMoreMaxLines = readMoreMaxLines,
        readMoreStyle = readMoreStyle,
        readMoreTextPosition = readMoreTextPosition,
        readLessText = readLessText,
        readLessStyle = readLessStyle,
        readLessTextPosition = readLessTextPosition,
        maxLinesExpanded = maxLinesExpanded
    )
}

@Composable
private fun ReadMoreText(
    text: AnnotatedString,
    expanded: Boolean,
    modifier: Modifier,
    onExpandedChange: () -> Unit,
    style: TextStyle,
    onTextLayout: (textLayoutResult: TextLayoutResult) -> Unit,
    softWrap: Boolean,
    readMoreText: String,
    readMoreMaxLines: Int,
    readMoreStyle: SpanStyle,
    readMoreTextPosition: TextPosition,
    readLessText: String,
    readLessStyle: SpanStyle,
    readLessTextPosition: TextPosition,
    maxLinesExpanded: Int?
) {
    require(readMoreMaxLines > 0) { "readMoreMaxLines should be greater than 0" }

    val readMoreTextWithStyle = remember(readMoreText, readMoreStyle) {
        buildAnnotatedString {
            if (readMoreText.isNotEmpty()) {
                withStyle(readMoreStyle) {
                    append(readMoreText.replace(" ", Typography.nbsp.toString()))
                }
            }
        }
    }

    val readLessTextWithStyle = remember(readLessText, readLessStyle) {
        buildAnnotatedString {
            if (readLessText.isNotEmpty()) {
                withStyle(readLessStyle) {
                    append(readLessText)
                }
            }
        }
    }

    val state = remember(text, readMoreMaxLines) {
        ReadMoreState(
            originalText = text,
            readMoreMaxLines = readMoreMaxLines
        )
    }

    val currentText = buildAnnotatedString {
        if (expanded) {
            append(text)
            if (readLessText.isNotEmpty() && readLessTextPosition == TextPosition.Inline) {
                pushStringAnnotation(tag = READ_LESS_TAG, annotation = "")
                append(readLessTextWithStyle)
                pop()
            }
        } else {
            val collapsedText = state.collapsedText
            if (collapsedText.isNotEmpty()) {
                append(collapsedText)

                if (readMoreText.isNotEmpty() && readMoreTextPosition == TextPosition.Inline) {
                    append(ELLIPSIS_WITH_SPACE)
                    pushStringAnnotation(tag = READ_MORE_TAG, annotation = "")
                    withStyle(readMoreStyle) {
                        append(readMoreText)
                    }
                    pop()
                } else {
                    append(ELLIPSIS)
                }
            } else {
                append(text)
            }
        }
    }

    Column {
        Box(
            modifier = modifier
                .let {
                    if (expanded && maxLinesExpanded != null) {
                        it.heightIn(
                            max = with(LocalDensity.current) {
                                style.lineHeight.toDp() * maxLinesExpanded
                            }
                        ).verticalScroll(rememberScrollState())
                    } else {
                        it
                    }
                }
        ) {
            ClickableText(
                modifier = Modifier.animateContentSize(),
                text = currentText,
                style = style,
                onTextLayout = {
                    state.onTextLayout(it)
                    onTextLayout(it)
                },
                overflow = TextOverflow.Ellipsis,
                softWrap = softWrap,
                maxLines = if (expanded) Int.MAX_VALUE else readMoreMaxLines,
                onClick = { offset ->
                    currentText.getStringAnnotations(READ_MORE_TAG, offset, offset)
                        .firstOrNull()?.let { onExpandedChange() }
                    currentText.getStringAnnotations(READ_LESS_TAG, offset, offset)
                        .firstOrNull()?.let { onExpandedChange() }
                }
            )
            if (!expanded) {
                BasicText(
                    text = Typography.ellipsis.toString(),
                    onTextLayout = { state.onOverflowTextLayout(it) },
                    modifier = Modifier.notDraw(),
                    style = style
                )
                BasicText(
                    text = readMoreTextWithStyle,
                    onTextLayout = { state.onReadMoreTextLayout(it) },
                    modifier = Modifier.notDraw(),
                    style = style.merge(readMoreStyle)
                )
            }
        }
        if (readLessTextPosition is TextPosition.NewLine) {
            Spacer(Modifier.height(readLessTextPosition.paddingTop))
        }

        if (!expanded && readMoreText.isNotEmpty() && readMoreTextPosition == TextPosition.NewLine()) {
            BasicText(
                text = readMoreTextWithStyle,
                style = style.merge(readMoreStyle),
                modifier = Modifier
                    .padding(top = Dimens.spacingTiny)
                    .clickable { onExpandedChange() }
            )
        }

        if (expanded && readLessText.isNotEmpty() && readLessTextPosition == TextPosition.NewLine()) {
            BasicText(
                text = readLessTextWithStyle,
                style = style.merge(readLessStyle),
                modifier = Modifier
                    .padding(top = Dimens.spacingTiny)
                    .clickable { onExpandedChange() }
            )
        }
    }
}

private fun Modifier.notDraw(): Modifier = then(NotDrawModifier)

private object NotDrawModifier : DrawModifier {
    override fun ContentDrawScope.draw() {}
}

@Stable
private class ReadMoreState(
    private val originalText: AnnotatedString,
    private val readMoreMaxLines: Int
) {
    private var textLayout: TextLayoutResult? = null
    private var overflowTextLayout: TextLayoutResult? = null
    private var readMoreTextLayout: TextLayoutResult? = null

    private var _collapsedText: AnnotatedString by mutableStateOf(AnnotatedString(""))

    var collapsedText: AnnotatedString
        get() = _collapsedText
        internal set(value) {
            if (value != _collapsedText) {
                _collapsedText = value
            }
        }

    fun onTextLayout(result: TextLayoutResult) {
        val lastLineIndex = readMoreMaxLines - 1
        val previous = textLayout
        val old = previous != null &&
            previous.lineCount >= readMoreMaxLines &&
            previous.isLineEllipsized(lastLineIndex)
        val new = result.lineCount >= readMoreMaxLines &&
            result.isLineEllipsized(lastLineIndex)
        if (previous != result && old != new) {
            textLayout = result
            updateCollapsedText()
        }
    }

    fun onOverflowTextLayout(result: TextLayoutResult) {
        if (overflowTextLayout?.size?.width != result.size.width) {
            overflowTextLayout = result
            updateCollapsedText()
        }
    }

    fun onReadMoreTextLayout(result: TextLayoutResult) {
        if (readMoreTextLayout?.size?.width != result.size.width) {
            readMoreTextLayout = result
            updateCollapsedText()
        }
    }

    private fun updateCollapsedText() {
        val lastLineIndex = readMoreMaxLines - 1
        val textLayout = textLayout ?: return
        val overflowTextLayout = overflowTextLayout ?: return
        val readMoreTextLayout = readMoreTextLayout ?: return
        if (textLayout.lineCount >= readMoreMaxLines && textLayout.isLineEllipsized(lastLineIndex)) {
            val countUntilMaxLine = textLayout.getLineEnd(readMoreMaxLines - 1, visibleEnd = true)
            val readMoreWidth = overflowTextLayout.size.width + readMoreTextLayout.size.width
            val maximumWidth = textLayout.size.width - readMoreWidth
            var replacedEndIndex = countUntilMaxLine + 1
            var currentTextBounds: Rect
            do {
                replacedEndIndex -= 1
                currentTextBounds = textLayout.getCursorRect(replacedEndIndex)
            } while (currentTextBounds.left > maximumWidth)
            collapsedText = originalText.subSequence(startIndex = 0, endIndex = replacedEndIndex)
        }
    }
}

@Preview
@Composable
private fun ReadMoreTextPreview() {
    var expanded by remember { mutableStateOf(false) }

    ReadMoreText(
        text = "I`m a passionate young man with a dream of revolutionizing science. My vision is to have a new 3D printer that can push the boundaries of research and innovation. Every night, I imagine the possibilities that this advanced technology could unlock in various scientific fields.",
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        readMoreTextPosition = TextPosition.NewLine(),
        readLessTextPosition = TextPosition.NewLine(),
        style = TextStyle(),
        readMoreStyle = SpanStyle(),
        readLessText = "Read less",
        maxLinesExpanded = 5,
        readMoreText = "Read more",
    )
}