package com.urlaunched.android.design.ui.question.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.urlaunched.android.design.ui.question.models.QuestionColors
import com.urlaunched.android.design.ui.question.models.QuestionConstants
import com.urlaunched.android.design.ui.question.models.QuestionDimens

@Composable
fun QuestionItem(
    modifier: Modifier = Modifier,
    questionText: String,
    answerText: String,
    questionTextStyle: TextStyle,
    answerTextStyle: TextStyle,
    questionDimens: QuestionDimens = QuestionDimens(),
    questionConstants: QuestionConstants = QuestionConstants(),
    questionColors: QuestionColors = QuestionColors(),
    arrowButton: @Composable (rotation: Float) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val rotateState by animateFloatAsState(
        targetValue = if (expanded) questionConstants.rotateAngleArrow else questionConstants.defaultAngleArrow,
        label = questionConstants.rotateStateArrowLabel
    )

    Column(
        modifier
            .border(
                border = BorderStroke(width = questionDimens.borderWidth, color = questionColors.borderColor),
                shape = RoundedCornerShape(questionDimens.borderShape)
            )
            .toggleable(
                value = expanded,
                onValueChange = { value ->
                    expanded = value
                },
                indication = null,
                interactionSource = null
            )
            .padding(horizontal = questionDimens.horizontalPadding)
    ) {
        Row(
            modifier = Modifier.defaultMinSize(minHeight = questionDimens.minHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = questionText,
                style = questionTextStyle,
                color = questionColors.questionTextColor
            )

            Spacer(modifier = Modifier.width(questionDimens.questionButtonSpacing))

            arrowButton(rotateState)
        }

        Divider(color = questionColors.driverColor)

        AnimatedVisibility(visible = expanded) {
            Text(
                modifier = Modifier.padding(vertical = questionDimens.answerTextVerticalPadding),
                text = answerText,
                style = answerTextStyle,
                color = questionColors.answerTextColor
            )
        }
    }
}