package com.urlaunched.android.models.presentation.utils

import android.annotation.SuppressLint

@SuppressLint("ExperimentalAnnotationRetention")
@RequiresOptIn(
    message = "This method should be used very rarely because it downloads the original image, which can waste a lot of Internet traffic",
    level = RequiresOptIn.Level.WARNING,
)
annotation class SensitiveApi
