package com.urlaunched.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet as defaultBottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument>? = null,
    content: @Composable (backStackEntry: NavBackStackEntry) -> Unit
) {
    defaultBottomSheet(
        route = route,
        arguments = arguments.orEmpty()
    ) { backStackEntry ->
        LogFirebasePerformance(route = route)

        content(
            backStackEntry
        )
    }
}