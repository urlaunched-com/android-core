package com.urlaunched.android.design.ui.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheet
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun BaseBottomSheet(
    modifier: Modifier = Modifier,
    isShow: Boolean,
    onDismiss: () -> Unit,
    skipPartiallyExpanded: Boolean = true,
    swipeToDismissFraction: Float = 0.2f,
    background: Color = Color.White,
    shape: RoundedCornerShape =
        RoundedCornerShape(topStart = Dimens.cornerRadiusNormal, topEnd = Dimens.cornerRadiusNormal),
    partiallyExpandedFraction: Float = 0.6f,
    content: @Composable () -> Unit
) {
    var contentHeight by remember { mutableStateOf(Dimens.zeroDp) }

    val partiallyExpanded = SheetDetent(identifier = PARTIALLY_EXPANDED_ID) { containerHeight, _ ->
        containerHeight * partiallyExpandedFraction
    }

    val density = LocalDensity.current

    val sheetState = rememberModalBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = if (skipPartiallyExpanded) {
            listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
        } else {
            listOf(SheetDetent.Hidden, partiallyExpanded, SheetDetent.FullyExpanded)
        },
        positionalThreshold = { contentHeight * swipeToDismissFraction }
    )

    ModalBottomSheet(
        state = sheetState,
        onDismiss = onDismiss
    ) {
        Scrim()

        Sheet(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(background)
        ) {
            Box(
                modifier = Modifier.onSizeChanged {
                    contentHeight = (it.height / density.density).dp
                }
            ) {
                content()
            }
        }
    }

    LaunchedEffect(isShow) {
        sheetState.currentDetent = if (isShow) {
            SheetDetent.FullyExpanded
        } else {
            SheetDetent.Hidden
        }
    }
}

private const val PARTIALLY_EXPANDED_ID = "partiallyExpanded"