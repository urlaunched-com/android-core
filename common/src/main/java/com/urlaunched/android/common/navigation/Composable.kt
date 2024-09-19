package com.urlaunched.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable as defaultComposable

fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument>? = null,
    content: @Composable (backStackEntry: NavBackStackEntry) -> Unit
) {
    defaultComposable(
        route = route,
        arguments = arguments.orEmpty()
    ) { backStackEntry ->
        LogFirebasePerformance(route = route)

        content(backStackEntry)
    }
}