package com.example.markdownreader.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.TextDecrease
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            // 文件名过长时可左右拖动查看完整内容（不省略号截断）
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                    text = title,
                    maxLines = 1,
                    softWrap = false
                )
            }
        },
        actions = {
            ReaderBarActions(
                isDarkMode = isDarkMode,
                isEditMode = isEditMode,
                hasContent = hasContent,
                onToggleTheme = onToggleTheme,
                onToggleEditMode = onToggleEditMode,
                onShare = onShare,
                onExportPdf = onExportPdf
            )
        },
        // 竖屏顶栏从默认 64dp 收窄到 48dp，把竖向空间留给正文
        expandedHeight = 48.dp,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * 顶栏与竖向工具条共用的按钮组（按钮本身自适应横/竖两个方向的容器）。
 * 字号增减按钮只在横屏竖向工具条里展示，竖屏顶栏空间有限，
 * 字号入口由双指捏合 + 缩放控制条承担。
 */
@Composable
fun ReaderBarActions(
    isDarkMode: Boolean,
    isEditMode: Boolean,
    hasContent: Boolean,
    onToggleTheme: () -> Unit,
    onToggleEditMode: () -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit,
    onIncreaseFontScale: () -> Unit = {},
    onDecreaseFontScale: () -> Unit = {},
    showFontScaleControls: Boolean = false
) {
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
    if (showFontScaleControls) {
        IconButton(onClick = onDecreaseFontScale) {
            Icon(
                imageVector = Icons.Outlined.TextDecrease,
                contentDescription = "缩小字号"
            )
        }
        IconButton(onClick = onIncreaseFontScale) {
            Icon(
                imageVector = Icons.Outlined.TextIncrease,
                contentDescription = "放大字号"
            )
        }
    }
}

/**
 * 横屏用的左侧竖向工具条：按钮竖排、不显示标题，把竖向空间全部留给正文。
 */
@Composable
fun ReaderSideRail(
    isDarkMode: Boolean,
    isEditMode: Boolean,
    hasContent: Boolean,
    onToggleTheme: () -> Unit,
    onToggleEditMode: () -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit,
    onIncreaseFontScale: () -> Unit,
    onDecreaseFontScale: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .width(56.dp)
                .fillMaxHeight()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            ReaderBarActions(
                isDarkMode = isDarkMode,
                isEditMode = isEditMode,
                hasContent = hasContent,
                onToggleTheme = onToggleTheme,
                onToggleEditMode = onToggleEditMode,
                onShare = onShare,
                onExportPdf = onExportPdf,
                onIncreaseFontScale = onIncreaseFontScale,
                onDecreaseFontScale = onDecreaseFontScale,
                showFontScaleControls = true
            )
        }
    }
}
