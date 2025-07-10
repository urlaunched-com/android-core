package com.urlaunched.android.design.ui.menuItem.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.urlaunched.android.design.ui.menuItem.models.MenuItem
import com.urlaunched.android.design.ui.menuItem.models.MenuItemColors
import com.urlaunched.android.design.ui.menuItem.models.MenuItemDimens

@Composable
fun MenuItemWrapper(
    modifier: Modifier = Modifier,
    menuItem: List<MenuItem>,
    textStyle: TextStyle,
    trailingIcon: @Composable () -> Unit,
    menuItemColors: MenuItemColors = MenuItemColors(),
    menuItemDimens: MenuItemDimens = MenuItemDimens()
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(menuItem) { item ->
            MenuItem(
                titleResource = item.textResources,
                textStyle = textStyle,
                onClick = item.onClick,
                trailingIcon = trailingIcon,
                menuItemColors = menuItemColors,
                menuItemDimens = menuItemDimens
            )

            Spacer(modifier = Modifier.height(menuItemDimens.spacerHeight))
        }
    }
}