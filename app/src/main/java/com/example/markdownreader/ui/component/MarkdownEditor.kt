package com.example.markdownreader.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarkdownEditor(
    content: String,
    fontScale: Float = 1f,
    onContentChange: (String) -> Unit,
    onZoom: (Float) -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier
) {
    val editorState = rememberTextFieldState(content)
    val currentOnContentChange by rememberUpdatedState(onContentChange)

    // 编辑内容回传给 ViewModel（state API 没有 onValueChange 回调，用 snapshotFlow 观察）
    LaunchedEffect(editorState) {
        snapshotFlow { editorState.text }.collect { currentOnContentChange(it.toString()) }
    }

    // 外部内容变化（重新载入文件等）时整体替换；编辑器自身输入的回流文本相同，不会进入此分支
    LaunchedEffect(content) {
        if (content != editorState.text.toString()) {
            editorState.setTextAndPlaceCursorAtEnd(content)
        }
    }

    // 不要给 BasicTextField 挂 Modifier.verticalScroll：那样文本域高度等于整篇文档，
    // 而 Compose 焦点系统在节点首次获焦时会“把整个节点滚入可视区”，对整篇文档的节点
    // 就等于滚回文首——表现为进入编辑后第一次点击必跳顶。
    // 改用 TextFieldState 重载并提升其内部 scrollState：文本域保持一屏高，获焦时的
    // 滚入请求天然是空操作；滚动条、进度同步与输入时光标跟随都走同一个 state。
    BasicTextField(
        state = editorState,
        scrollState = scrollState,
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = (14 * fontScale).sp,
            lineHeight = (22 * fontScale).sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxSize()
            .pinchZoomText(onZoom = onZoom)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}
