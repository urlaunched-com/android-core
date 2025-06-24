package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.player.models.BottomItemColors
import com.urlaunched.android.design.ui.player.models.BottomItemDimens

@Composable
internal fun BottomItem(
    modifier: Modifier = Modifier,
    title: String,
    bottomItemDimens: BottomItemDimens = BottomItemDimens(),
    bottomItemColors: BottomItemColors = BottomItemColors(),
    textStyle: TextStyle,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.cornerRadiusNormalSpecial))
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
            .padding(Dimens.spacingTiny),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(bottomItemDimens.actionItemWidth, bottomItemDimens.actionItemHeight)
                .clip(RoundedCornerShape(Dimens.cornerRadiusBigSpecial))
                .background(bottomItemColors.background),
            content = content,
            contentAlignment = Alignment.Center
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSmall))

        Text(
            text = title,
            color = if (enabled) bottomItemColors.enabledTextColor else bottomItemColors.disabledTextColor,
            style = textStyle
        )
    }
}