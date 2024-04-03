package com.urlaunched.android.common.resources

import android.content.res.Resources
import java.io.InputStream

class AppResourcesImpl(private val resources: Resources) : AppResources {
    override fun getString(id: Int): String = resources.getString(id)
    override fun getString(id: Int, vararg args: Any): String = resources.getString(id, *args)
    override fun openRawResource(id: Int): InputStream = resources.openRawResource(id)
}