package com.urlaunched.android.common.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat

object IntentHelper {
    private const val PACKAGE_SCHEME = "package"
    private const val PLAIN_TEXT = "text/plain"

    fun openLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        ContextCompat.startActivity(context, intent, null)
    }

    fun openCustomTab(context: Context, url: String) {
        CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
    }

    fun openSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(PACKAGE_SCHEME, context.packageName, null)
        )
        ContextCompat.startActivity(context, intent, null)
    }

    fun openEmail(context: Context, email: String) {
        runCatching {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("mailto:?to=$email")
            }.let { context.startActivity(it) }
        }
    }

    fun openEmail(context: Context, email: String, subject: String, text: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("mailto:?subject=$subject&body=$text&to=$email")
            }
            context.startActivity(intent)
        }
    }

    fun sharePlainText(context: Context, text: String) {
        context.startActivity(
            ShareCompat
                .IntentBuilder(context)
                .setText(text)
                .setType(PLAIN_TEXT)
                .createChooserIntent()
        )
    }
}