package com.husseinelfeky.smartbank.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object Navigator {

    fun navigateToAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}
