package com.urlaunched.android.design.ui.tutorial.utils

import com.urlaunched.android.design.ui.tutorial.models.TutorialLayoutCoordinates

object TutorialUtils {
    // Get max value of width or height for calculation correct radius
    internal fun getMaxSide(layoutCoordinates: TutorialLayoutCoordinates?) =
        maxOf(layoutCoordinates?.width ?: 0f, layoutCoordinates?.height ?: 0f)
}