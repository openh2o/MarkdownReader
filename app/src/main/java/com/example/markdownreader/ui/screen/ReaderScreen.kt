package com.example.markdownreader.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.markdownreader.ui.component.MarkdownContent
import com.example.markdownreader.ui.component.MarkdownEditor
import com.example.markdownreader.ui.component.ReaderTopBar
import com.example.markdownreader.ui.theme.MarkdownReaderTheme
import com.example.markdownreader.util.PdfExporter
import com.example.markdownreader.util.ShareHelper
import com.example.markdownreader.viewmodel.ReaderViewModel

@Composable
fun ReaderScreen(viewModel: ReaderViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val editContent by viewModel.editContent.collectAsState()
    val context = LocalContext.current

    MarkdownReaderTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                ReaderTopBar(
                    title = uiState.markdownFile?.name ?: "MD阅读器",
                    isDarkMode = isDarkMode,
                    isEditMode = isEditMode,
                    onToggleTheme = { viewModel.toggleDarkMode() },
                    onToggleEditMode = { viewModel.toggleEditMode() },
                    onShare = {
                        uiState.markdownFile?.let { file ->
                            ShareHelper.shareText(context, file.content)
                        }
                    },
                    onExportPdf = {
                        uiState.markdownFile?.let { file ->
                            PdfExporter.export(context, file.name, file.content)
                        }
                    },
                    hasContent = uiState.markdownFile != null
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    uiState.markdownFile != null -> {
                        if (isEditMode) {
                            MarkdownEditor(
                                content = editContent,
                                onContentChange = { viewModel.updateEditContent(it) }
                            )
                        } else {
                            MarkdownContent(content = uiState.markdownFile!!.content)
                        }
                    }
                    else -> {
                        EmptyScreen()
                    }
                }
            }
        }
    }
}
