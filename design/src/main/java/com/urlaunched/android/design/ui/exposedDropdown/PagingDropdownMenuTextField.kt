package com.urlaunched.android.design.ui.exposedDropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.paging.PagingData
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.exposedDropdown.constants.DropdownMenuDimens
import com.urlaunched.android.design.ui.paging.PagingColumn
import com.urlaunched.android.design.ui.scrollbar.LazyColumnScrollbar
import com.urlaunched.android.design.ui.scrollbar.ScrollbarSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : Any> PagingDropdownMenuTextField(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    pagingDataFlow: Flow<PagingData<T>>,
    text: String,
    onValueChange: (text: String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (isExpanded: Boolean) -> Unit,
    onDismiss: () -> Unit,
    placeHolder: String? = null,
    readonly: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
    maxHeight: Dp = Dp.Unspecified,
    settings: ScrollbarSettings = ScrollbarSettings.Default,
    contentPadding: PaddingValues = PaddingValues(),
    menuShape: Shape = RoundedCornerShape(Dimens.cornerRadiusNormalSpecial),
    maxMenuHeight: Dp = Dp.Unspecified,
    menuBackground: Color = Color.White,
    menuBorder: BorderStroke? = BorderStroke(
        width = DropdownMenuDimens.borderThickness,
        color = Color.Gray
    ),
    trailingIcon: @Composable (() -> Unit)? = null,
    showSnackbar: suspend (message: String) -> Unit,
    placeholderItem: @Composable (LazyItemScope.(index: Int) -> Unit),
    itemKey: ((item: T) -> Any)?,
    item: @Composable (LazyItemScope.(index: Int, itemCount: Int, item: T) -> Unit)
) {
    CustomDropdownMenuTextField(
        modifier = modifier,
        menuModifier = menuModifier,
        text = text,
        onValueChange = onValueChange,
        expanded = expanded,
        placeHolder = placeHolder,
        readOnly = readonly,
        onExpandedChange = onExpandedChange,
        onDismiss = onDismiss,
        menuShape = menuShape,
        maxMenuHeight = maxMenuHeight,
        menuBackground = menuBackground,
        menuBorder = menuBorder,
        trailingIcon = trailingIcon
    ) {
        LazyColumnScrollbar(
            modifier = Modifier.heightIn(max = maxHeight),
            state = lazyListState,
            settings = settings
        ) {
            PagingColumn(
                modifier = Modifier.heightIn(max = maxHeight),
                state = lazyListState,
                pagingDataFlow = pagingDataFlow,
                showSnackbar = showSnackbar,
                horizontalAlignment = Alignment.CenterHorizontally,
                placeholderItem = placeholderItem,
                contentPadding = contentPadding,
                itemKey = itemKey,
                item = item
            )
        }
    }
}

@Preview
@Composable
private fun PagingDropdownMenuPreview() {
    var expanded by remember { mutableStateOf(false) }

    PagingDropdownMenuTextField(
        text = "Expanded menu",
        expanded = expanded,
        onExpandedChange = { expanded = it },
        onDismiss = { expanded = false },
        pagingDataFlow = flowOf(PagingData.from(listOf<Int>())),
        showSnackbar = {},
        onValueChange = {},
        itemKey = {},
        placeholderItem = {}
    ) { index, _, _ ->
        Text(
            text = "Menu item $index",
            modifier = Modifier.padding(vertical = Dimens.spacingSmallSpecial)
        )
    }
}