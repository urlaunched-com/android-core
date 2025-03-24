package com.urlaunched.android.pdf.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
    view.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    )
    val bitmap = Bitmap.createBitmap(
        view.measuredWidth,
        view.measuredHeight,
        Bitmap.Config.ARGB_8888
    )
    bitmap.density = Bitmap.DENSITY_NONE

    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    view.draw(canvas)

    return bitmap
}