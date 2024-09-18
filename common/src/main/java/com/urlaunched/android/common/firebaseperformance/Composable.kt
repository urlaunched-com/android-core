package com.urlaunched.android.common.firebaseperformance

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable as defaultComposable

fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument>? = null,
    content: @Composable () -> Unit
) {
    defaultComposable(
        route = route,
        arguments = arguments.orEmpty()
    ) {
        LogFirebasePerformance(route = route)

        content()
    }
}