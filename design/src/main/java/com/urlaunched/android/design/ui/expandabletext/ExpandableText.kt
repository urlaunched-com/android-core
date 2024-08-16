package com.urlaunched.android.design.ui.expandabletext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int,
    textStyle: TextStyle,
    expandTextStyle: TextStyle,
    expandText: String,
    collapseText: String,
    expandSuffix: String
) {
    var isExpanded by remember { mutableStateOf(value = false) }
    var adjustedText by remember { mutableStateOf(value = text) }
    var showOverflow by remember { mutableStateOf(value = false) }

    val annotatedText = buildAnnotatedString {
        if (isExpanded) {
            append(text)
            append(SPACE)

            pushStringAnnotation(tag = READ_LESS_TAG, annotation = READ_LESS_ANNOTATION)
            append(collapseText)
            addStyle(
                style = expandTextStyle.toSpanStyle(),
                start = text.length,
                end = text.length + collapseText.length + 1
            )
            pop()
        } else {
            append(adjustedText)

            if (showOverflow) {
                append(expandSuffix)
                pushStringAnnotation(tag = READ_MORE_TAG, annotation = READ_MORE_ANNOTATION)
                append(expandText)
                addStyle(
                    style = expandTextStyle.toSpanStyle(),
                    start = adjustedText.length + expandSuffix.length,
                    end = adjustedText.length + expandSuffix.length + expandText.length
                )
                pop()
            }
        }
    }

    Box(modifier = modifier) {
        ClickableText(
            text = annotatedText,
            style = textStyle,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
            onTextLayout = { layoutResult ->
                if (!isExpanded && layoutResult.hasVisualOverflow) {
                    showOverflow = true
                    val lastCharIndex = layoutResult.getLineEnd(lineIndex = maxLines - 1)
                    adjustedText = text
                        .substring(startIndex = 0, endIndex = lastCharIndex)
                        .dropLast(expandText.length)
                        .dropLastWhile { it == SPACE_CHAR || it == DOT_CHAR }
                }
            },
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = READ_LESS_TAG,
                    start = offset,
                    end = offset + collapseText.length
                ).firstOrNull()?.let {
                    isExpanded = !isExpanded
                }
                annotatedText.getStringAnnotations(
                    tag = READ_MORE_TAG,
                    start = offset,
                    end = offset + expandText.length - expandSuffix.length
                ).firstOrNull()?.let {
                    isExpanded = !isExpanded
                }
            }
        )
    }
}

private const val SPACE = " "
private const val SPACE_CHAR = ' '
private const val DOT_CHAR = '.'

private const val READ_MORE_TAG = "read_more_tag"
private const val READ_MORE_ANNOTATION = "read_more_annotation"
private const val READ_LESS_TAG = "read_less_tag"
private const val READ_LESS_ANNOTATION = "read_less_annotation"