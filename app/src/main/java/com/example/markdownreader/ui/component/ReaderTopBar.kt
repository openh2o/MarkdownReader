package com.example.markdownreader.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderTopBar(
    title: String,
    isDarkMode: Boolean,
    isEditMode: Boolean,
    onToggleTheme: () -> Unit,
    onToggleEditMode: () -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit,
    hasContent: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            // 阅览/编辑模式切换
            if (hasContent) {
                IconButton(onClick = onToggleEditMode) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Outlined.Visibility
                        else Icons.Outlined.Edit,
                        contentDescription = if (isEditMode) "切换到阅览模式" else "切换到编辑模式"
                    )
                }
            }
            // 暗色/亮色切换
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Outlined.LightMode
                    else Icons.Outlined.DarkMode,
                    contentDescription = if (isDarkMode) "切换到亮色模式" else "切换到暗色模式"
                )
            }
            if (hasContent) {
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "分享"
                    )
                }
                IconButton(onClick = onExportPdf) {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = "导出PDF"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
