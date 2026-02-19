package com.example.markdownreader.util

import android.content.Context
import android.content.Intent

object ShareHelper {
    fun shareText(context: Context, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "分享 Markdown")
        context.startActivity(shareIntent)
    }
}
