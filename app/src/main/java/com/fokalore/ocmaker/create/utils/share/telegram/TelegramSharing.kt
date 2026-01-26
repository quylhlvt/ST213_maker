package com.fokalore.ocmaker.create.utils.share.telegram

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.fokalore.ocmaker.create.R


object TelegramSharing {

    fun importToTelegram(context: Context, uriList: List<Uri>) {
        if (uriList.isEmpty()) {
            Toast.makeText(context, "No images to share", Toast.LENGTH_SHORT).show()
            return
        }

        val list = ArrayList(uriList)

        Log.d("TelegramSharing", "=== Sharing to Telegram ===")
        Log.d("TelegramSharing", "URIs: ${list.size}")
        Log.d("TelegramSharing", "Android version: ${Build.VERSION.SDK_INT}")

        // Kiểm tra Telegram có cài không
        val telegramInstalled = isTelegramInstalled(context)
        Log.d("TelegramSharing", "Telegram installed: $telegramInstalled")

        if (!telegramInstalled) {
            Toast.makeText(
                context,
                context.getString(R.string.no_app_found_to_handle_this_action),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Grant permissions
        list.forEach { uri ->
            try {
                context.grantUriPermission(
                    "org.telegram.messenger",
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                Log.d("TelegramSharing", "Granted permission for: $uri")
            } catch (e: Exception) {
                Log.e("TelegramSharing", "Grant permission failed: ${e.message}")
            }
        }


        // Cách 1: Intent đặc biệt của Telegram
        val telegramIntent = Intent("org.telegram.messenger.CREATE_STICKER_PACK").apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, list)
            putExtra("IMPORTER", context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
            setPackage("org.telegram.messenger")
        }

        try {
            context.startActivity(telegramIntent)
            Log.d("TelegramSharing", "Started with CREATE_STICKER_PACK")
            return
        } catch (e: Exception) {
            Log.w("TelegramSharing", "CREATE_STICKER_PACK failed: ${e.message}")
        }
    }
    private fun isTelegramInstalled(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    "org.telegram.messenger",
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo("org.telegram.messenger", 0)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}