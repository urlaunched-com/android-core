package com.urlaunched.android.common.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

interface Router {
    fun route(parentRoute: String): String
    fun navigate(
        parentRoute: String,
        navController: NavController,
        args: NavArgs? = null,
        navOptions: NavOptions? = null
    )

    interface NavArgs
}