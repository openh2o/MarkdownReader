package com.example.markdownreader

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Display
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
        requestPreferredRefreshRate()
        handleIntent(intent)
        setContent {
            ReaderScreen(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * 请求屏幕当前分辨率下的最高刷新率（很多 ROM 对未主动申请的应用默认锁 60Hz）。
     * 旋转屏幕时 activity 不重建（已声明 configChanges），无需重新请求。
     */
    private fun requestPreferredRefreshRate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }
        val current = display?.mode ?: return
        val best = display.supportedModes
            .filter {
                it.physicalWidth == current.physicalWidth &&
                        it.physicalHeight == current.physicalHeight
            }
            .maxByOrNull { it.refreshRate } ?: return
        window.attributes = window.attributes.apply { preferredDisplayModeId = best.modeId }
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
