package com.example.markdownreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.markdownreader.ui.screen.ReaderScreen
import com.example.markdownreader.viewmodel.ReaderViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            ReaderScreen(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                intent.data?.let { uri ->
                    viewModel.loadFromUri(uri, contentResolver)
                }
            }
            Intent.ACTION_SEND -> {
                @Suppress("DEPRECATION")
                val streamUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                if (streamUri != null) {
                    viewModel.loadFromUri(streamUri, contentResolver)
                } else {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                        viewModel.loadFromText(text)
                    }
                }
            }
        }
    }
}
