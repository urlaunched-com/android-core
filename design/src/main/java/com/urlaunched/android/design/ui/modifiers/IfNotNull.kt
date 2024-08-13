package com.urlaunched.android.design.ui.modifiers

import androidx.compose.ui.Modifier

inline fun <T : Any> Modifier.ifNotNull(value: T?, builder: (T) -> Modifier): Modifier =
    then(if (value != null) builder(value) else Modifier)