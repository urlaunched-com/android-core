package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.urlaunched.android.design.ui.player.models.BottomBarItem
import com.urlaunched.android.design.ui.player.models.BottomItemColors
import com.urlaunched.android.design.ui.player.models.BottomPanelDimens

@Composable
internal fun BottomPanel(
    bottomBarItems: List<BottomBarItem>,
    bottomTextStyle: TextStyle,
    bottomItemColors: BottomItemColors = BottomItemColors(),
    bottomPanelDimens: BottomPanelDimens = BottomPanelDimens()
) {
    Row(
        modifier = Modifier
            .padding(
                vertical = bottomPanelDimens.rowVerticalPadding,
                horizontal = bottomPanelDimens.rowHorizontalPadding
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(bottomPanelDimens.horizontalArrangementPadding)
    ) {
        bottomBarItems.forEach { item ->
            BottomItem(
                bottomItemColors = bottomItemColors,
                modifier = item.modifier,
                title = item.title,
                textStyle = bottomTextStyle,
                enabled = item.enabled,
                onClick = item.onClick,
                content = { item.icon() }
            )
        }
    }
}