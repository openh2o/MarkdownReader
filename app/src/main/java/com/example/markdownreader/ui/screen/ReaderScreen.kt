package com.example.markdownreader.ui.screen

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextDecrease
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.markdownreader.data.RecentFile
import com.example.markdownreader.ui.component.MarkdownContent
import com.example.markdownreader.ui.component.MarkdownEditor
import com.example.markdownreader.ui.component.ReaderScrollbar
import com.example.markdownreader.ui.component.ReaderSideRail
import com.example.markdownreader.ui.component.ReaderTopBar
import com.example.markdownreader.ui.theme.MarkdownReaderTheme
import com.example.markdownreader.util.PdfExporter
import com.example.markdownreader.util.ShareHelper
import com.example.markdownreader.util.findActivity
import com.example.markdownreader.viewmodel.ReaderUiState
import com.example.markdownreader.viewmodel.ReaderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ReaderScreen(viewModel: ReaderViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val editContent by viewModel.editContent.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val recentFiles by viewModel.recentFiles.collectAsState()
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // 部分提供方不支持持久化授权，本次会话内仍可读取
            }
            viewModel.loadFromUri(uri, context.contentResolver)
        }
    }
    val onPickFile: () -> Unit = { pickFileLauncher.launch(arrayOf("*/*")) }
    val onOpenRecent: (RecentFile) -> Unit = { recent ->
        viewModel.loadFromUri(Uri.parse(recent.uri), context.contentResolver)
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 缩放控制条：任何缩放操作都会刷新它的 2.5s 自动隐藏计时
    var zoomBarVisible by remember { mutableStateOf(false) }
    var zoomBarTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(zoomBarTick) {
        if (zoomBarTick > 0) {
            delay(2500)
            zoomBarVisible = false
        }
    }
    val showZoomBar: () -> Unit = {
        zoomBarVisible = true
        zoomBarTick++
    }
    val onZoom: (Float) -> Unit = { factor ->
        viewModel.scaleFont(factor)
        showZoomBar()
    }

    // 滚动状态提升到屏幕层：横竖屏切换、阅读/编辑模式切换时都不会丢失
    val readerScrollState = rememberScrollState()
    val editorScrollState = rememberScrollState()

    // 换了新文件（loadId 变化）时把滚动位置归零：从主页打开另一个文件不应继承
    // 上一个文件的阅读进度；同文件的模式切换副本 loadId 不变，位置照常保留。
    // ScrollState 的写入是挂起函数，须经协程归零——此时新文件尚在载入、正文未组合，不会闪动
    val coroutineScope = rememberCoroutineScope()
    var lastLoadId by remember { mutableLongStateOf(-1L) }
    if (uiState.loadId != lastLoadId) {
        lastLoadId = uiState.loadId
        coroutineScope.launch {
            readerScrollState.scrollTo(0)
            editorScrollState.scrollTo(0)
        }
    }

    // 阅读 <-> 编辑按进度比例同步位置，避免切换后跳回文档开头
    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            val fraction = if (readerScrollState.maxValue > 0)
                readerScrollState.value.toFloat() / readerScrollState.maxValue
            else 0f
            snapshotFlow { editorScrollState.maxValue }.filter { it > 0 }.first()
            editorScrollState.scrollTo((fraction * editorScrollState.maxValue).roundToInt())
        } else if (editorScrollState.maxValue > 0) {
            val fraction = editorScrollState.value.toFloat() / editorScrollState.maxValue
            snapshotFlow { readerScrollState.maxValue }.filter { it > 0 }.first()
            readerScrollState.scrollTo((fraction * readerScrollState.maxValue).roundToInt())
        }
    }

    MarkdownReaderTheme(darkTheme = isDarkMode) {
        HideStatusBarInLandscape(isLandscape)

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLandscape) {
                // 横屏：不显示顶栏，改为左侧竖向工具条，正文占满其余空间
                Scaffold { paddingValues ->
                    Row(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.displayCutout)
                    ) {
                        ReaderSideRail(
                            isDarkMode = isDarkMode,
                            isEditMode = isEditMode,
                            hasContent = uiState.markdownFile != null,
                            onHome = { viewModel.closeFile() },
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
                            onIncreaseFontScale = {
                                viewModel.increaseFontScale()
                                showZoomBar()
                            },
                            onDecreaseFontScale = {
                                viewModel.decreaseFontScale()
                                showZoomBar()
                            }
                        )
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        ReaderBody(
                            uiState = uiState,
                            isEditMode = isEditMode,
                            editContent = editContent,
                            fontScale = fontScale,
                            onZoom = onZoom,
                            onEditContentChange = { viewModel.updateEditContent(it) },
                            readerScrollState = readerScrollState,
                            editorScrollState = editorScrollState,
                            recentFiles = recentFiles,
                            onPickFile = onPickFile,
                            onOpenRecent = onOpenRecent,
                            onClearRecent = { viewModel.clearRecentFiles() },
                            onDismissError = { viewModel.dismissError() },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            } else {
                Scaffold(
                    topBar = {
                        ReaderTopBar(
                            title = uiState.markdownFile?.name ?: "MD阅读器",
                            isDarkMode = isDarkMode,
                            isEditMode = isEditMode,
                            hasContent = uiState.markdownFile != null,
                            onHome = { viewModel.closeFile() },
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
                            }
                        )
                    }
                ) { paddingValues ->
                    ReaderBody(
                        uiState = uiState,
                        isEditMode = isEditMode,
                        editContent = editContent,
                        fontScale = fontScale,
                        onZoom = onZoom,
                        onEditContentChange = { viewModel.updateEditContent(it) },
                        readerScrollState = readerScrollState,
                        editorScrollState = editorScrollState,
                        recentFiles = recentFiles,
                        onPickFile = onPickFile,
                        onOpenRecent = onOpenRecent,
                        onClearRecent = { viewModel.clearRecentFiles() },
                        onDismissError = { viewModel.dismissError() },
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                }
            }

            FontScaleBar(
                visible = zoomBarVisible,
                fontScale = fontScale,
                onIncrease = {
                    viewModel.increaseFontScale()
                    showZoomBar()
                },
                onDecrease = {
                    viewModel.decreaseFontScale()
                    showZoomBar()
                },
                onReset = {
                    viewModel.resetFontScale()
                    showZoomBar()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun ReaderBody(
    uiState: ReaderUiState,
    isEditMode: Boolean,
    editContent: String,
    fontScale: Float,
    onZoom: (Float) -> Unit,
    onEditContentChange: (String) -> Unit,
    readerScrollState: ScrollState,
    editorScrollState: ScrollState,
    recentFiles: List<RecentFile>,
    onPickFile: () -> Unit,
    onOpenRecent: (RecentFile) -> Unit,
    onClearRecent: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewportHeightPx by remember { mutableIntStateOf(0) }
    Box(modifier = modifier.onSizeChanged { viewportHeightPx = it.height }) {
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
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onPickFile) {
                        Text("选择 Markdown 文件")
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = onDismissError) {
                        Text("返回首页")
                    }
                }
            }
            uiState.markdownFile != null -> {
                if (isEditMode) {
                    MarkdownEditor(
                        content = editContent,
                        fontScale = fontScale,
                        onContentChange = onEditContentChange,
                        onZoom = onZoom,
                        scrollState = editorScrollState
                    )
                } else {
                    MarkdownContent(
                        content = uiState.markdownFile!!.content,
                        fontScale = fontScale,
                        onZoom = onZoom,
                        scrollState = readerScrollState
                    )
                }
                ReaderScrollbar(
                    scrollState = if (isEditMode) editorScrollState else readerScrollState,
                    viewportHeightPx = viewportHeightPx,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                )
            }
            else -> {
                EmptyScreen(
                    recentFiles = recentFiles,
                    onPickFile = onPickFile,
                    onOpenRecent = onOpenRecent,
                    onClearRecent = onClearRecent
                )
            }
        }
    }
}

/**
 * 横屏时隐藏系统状态栏（从屏幕边缘滑动可临时呼出），竖屏恢复显示。
 */
@Composable
private fun HideStatusBarInLandscape(isLandscape: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(isLandscape) {
        val window = view.context.findActivity()?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            if (isLandscape) {
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars())
            }
        }
        onDispose { }
    }
}

/**
 * 缩放控制条：捏合缩放时的反馈 + 按钮入口（竖屏顶栏不放字号按钮，
 * 避免挤占标题空间）；点击中间的百分比一键恢复 100%。
 */
@Composable
private fun FontScaleBar(
    visible: Boolean,
    fontScale: Float,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(150)) + slideInVertically(tween(150)) { it / 2 },
        exit = fadeOut(tween(200))
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 3.dp,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrease) {
                    Icon(
                        imageVector = Icons.Outlined.TextDecrease,
                        contentDescription = "缩小字号"
                    )
                }
                Text(
                    text = "${(fontScale * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onReset)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                IconButton(onClick = onIncrease) {
                    Icon(
                        imageVector = Icons.Outlined.TextIncrease,
                        contentDescription = "放大字号"
                    )
                }
            }
        }
    }
}
