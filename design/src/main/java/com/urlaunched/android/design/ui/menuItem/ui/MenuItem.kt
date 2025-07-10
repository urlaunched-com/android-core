package com.urlaunched.android.design.ui.menuItem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.urlaunched.android.design.ui.menuItem.models.MenuItemColors
import com.urlaunched.android.design.ui.menuItem.models.MenuItemDimens

@Composable
internal fun MenuItem(
    titleResource: Int,
    textStyle: TextStyle,
    menuItemColors: MenuItemColors,
    menuItemDimens: MenuItemDimens,
    onClick: () -> Unit,
    trailingIcon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(menuItemDimens.cornerRadiusShape))
            .background(color = menuItemColors.backgroundColor)
            .height(menuItemDimens.containerHeight)
            .clickable(onClick = onClick)
            .padding(
                start = menuItemDimens.startPadding,
                end = menuItemDimens.endPadding,
                top = menuItemDimens.topPadding,
                bottom = menuItemDimens.bottomPadding
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(titleResource),
            style = textStyle,
            color = menuItemColors.textColor
        )

        trailingIcon()
    }
}