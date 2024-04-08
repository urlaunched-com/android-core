package com.urlaunched.android.common.navigation

import androidx.navigation.NavHostController

fun NavHostController.hasDestinationInStack(destination: String): Boolean {
    var current = currentDestination
    while (current != null) {
        if (current.route?.startsWith(destination) == true) return true
        current = current.parent
    }
    return false
}