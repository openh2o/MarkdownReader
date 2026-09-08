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
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextDecrease
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderTopBar(
    title: String,
    isDarkMode: Boolean,
    isEditMode: Boolean,
    hasContent: Boolean,
    onHome: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleEditMode: () -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
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
            IconButton(onClick = onHome) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "回到主页"
                )
            }
            // 阅览/编辑、主题、分享、导出 PDF 收进“更多”菜单，顶栏只留两个图标
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "更多操作"
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (isEditMode) "切换到阅览模式" else "切换到编辑模式") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isEditMode) Icons.Outlined.Visibility
                            else Icons.Outlined.Edit,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onToggleEditMode()
                    },
                    enabled = hasContent
                )
                DropdownMenuItem(
                    text = { Text(if (isDarkMode) "切换到亮色模式" else "切换到暗色模式") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Outlined.LightMode
                            else Icons.Outlined.DarkMode,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onToggleTheme()
                    }
                )
                DropdownMenuItem(
                    text = { Text("分享文本") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Share, contentDescription = null)
                    },
                    onClick = {
                        menuExpanded = false
                        onShare()
                    },
                    enabled = hasContent
                )
                DropdownMenuItem(
                    text = { Text("导出 PDF") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.PictureAsPdf, contentDescription = null)
                    },
                    onClick = {
                        menuExpanded = false
                        onExportPdf()
                    },
                    enabled = hasContent
                )
            }
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
 * 横屏竖向工具条共用的按钮组（阅览/编辑、主题、分享、PDF、字号增减）。
 * 竖屏顶栏不使用此组件——竖屏的操作已收进“更多”菜单，字号入口由
 * 双指捏合 + 缩放控制条承担。
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
                imageVector = Icons.Outlined.Share,
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
 * 主页按钮固定在第一位。
 */
@Composable
fun ReaderSideRail(
    isDarkMode: Boolean,
    isEditMode: Boolean,
    hasContent: Boolean,
    onHome: () -> Unit,
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
            IconButton(onClick = onHome) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "回到主页"
                )
            }
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
