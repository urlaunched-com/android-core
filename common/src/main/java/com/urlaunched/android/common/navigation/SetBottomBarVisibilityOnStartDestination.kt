package com.urlaunched.android.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun SetBottomBarVisibilityOnStartDestination(
    navController: NavController,
    startDestination: String,
    showBottomBar: (isVisible: Boolean) -> Unit
) {
    val currentEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentEntry) {
        currentEntry?.destination?.route?.let { route ->
            showBottomBar(route == startDestination)
        }
    }
}