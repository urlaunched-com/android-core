package com.urlaunched.android.design.ui.progresstext.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.LayoutDirection

interface ProgressDirection {
    fun getStartOffset(layoutDirection: LayoutDirection): Offset
    fun getEndOffset(layoutDirection: LayoutDirection): Offset

    object Horizontal : ProgressDirection {
        override fun getStartOffset(layoutDirection: LayoutDirection): Offset = when (layoutDirection) {
            LayoutDirection.Ltr -> LeftToRight.getStartOffset(layoutDirection)
            LayoutDirection.Rtl -> RightToLeft.getStartOffset(layoutDirection)
        }
        override fun getEndOffset(layoutDirection: LayoutDirection): Offset = when (layoutDirection) {
            LayoutDirection.Ltr -> LeftToRight.getEndOffset(layoutDirection)
            LayoutDirection.Rtl -> RightToLeft.getEndOffset(layoutDirection)
        }
    }

    object LeftToRight : ProgressDirection {
        override fun getStartOffset(layoutDirection: LayoutDirection): Offset = Offset.Zero
        override fun getEndOffset(layoutDirection: LayoutDirection): Offset = Offset(Float.POSITIVE_INFINITY, 0f)
    }

    object RightToLeft : ProgressDirection {
        override fun getStartOffset(layoutDirection: LayoutDirection): Offset = Offset(Float.POSITIVE_INFINITY, 0f)
        override fun getEndOffset(layoutDirection: LayoutDirection): Offset = Offset.Zero
    }

    object TopToBottom : ProgressDirection {
        override fun getStartOffset(layoutDirection: LayoutDirection): Offset = Offset.Zero
        override fun getEndOffset(layoutDirection: LayoutDirection): Offset = Offset(0f, Float.POSITIVE_INFINITY)
    }

    object BottomToTop : ProgressDirection {
        override fun getStartOffset(layoutDirection: LayoutDirection): Offset = Offset(0f, Float.POSITIVE_INFINITY)
        override fun getEndOffset(layoutDirection: LayoutDirection): Offset = Offset.Zero
    }
}