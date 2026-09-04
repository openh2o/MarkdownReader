package com.example.markdownreader.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownDimens
import com.mikepenz.markdown.compose.LocalMarkdownPadding
import com.mikepenz.markdown.compose.elements.MarkdownCodeBackground
import com.mikepenz.markdown.compose.elements.MarkdownCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownCodeFence
import com.mikepenz.markdown.compose.elements.material.MarkdownBasicText
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.markdownDimens
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.utils.buildMarkdownAnnotatedString
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

@Composable
fun MarkdownContent(
    content: String,
    fontScale: Float = 1f,
    onZoom: (Float) -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier
) {
    val colors = markdownColor(
        dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )

    val typography = markdownTypography(
        h1 = TextStyle(
            fontSize = (26 * fontScale).sp,
            lineHeight = (36 * fontScale).sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        ),
        h2 = TextStyle(
            fontSize = (22 * fontScale).sp,
            lineHeight = (32 * fontScale).sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        ),
        h3 = TextStyle(
            fontSize = (19 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        ),
        h4 = TextStyle(
            fontSize = (17 * fontScale).sp,
            lineHeight = (26 * fontScale).sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        h5 = TextStyle(
            fontSize = (16 * fontScale).sp,
            lineHeight = (24 * fontScale).sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        h6 = TextStyle(
            fontSize = (15 * fontScale).sp,
            lineHeight = (22 * fontScale).sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        text = TextStyle(
            fontSize = (16 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp
        ),
        paragraph = TextStyle(
            fontSize = (16 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp
        ),
        quote = TextStyle(
            fontSize = (15 * fontScale).sp,
            lineHeight = (26 * fontScale).sp,
            fontStyle = FontStyle.Italic,
            letterSpacing = 0.25.sp
        ),
        code = TextStyle(
            fontSize = (14 * fontScale).sp,
            lineHeight = (22 * fontScale).sp,
            fontFamily = FontFamily.Monospace
        ),
        bullet = TextStyle(
            fontSize = (16 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            letterSpacing = 0.25.sp
        ),
        ordered = TextStyle(
            fontSize = (16 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            letterSpacing = 0.25.sp
        ),
        list = TextStyle(
            fontSize = (16 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            letterSpacing = 0.25.sp
        )
    )

    val padding = markdownPadding(
        block = 8.dp,
        list = 6.dp,
        listItemBottom = 4.dp,
        indentList = 12.dp
    )

    val dimens = markdownDimens(
        dividerThickness = 0.5.dp
    )

    val components = markdownComponents(
        horizontalRule = {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        },
        // 覆盖默认代码块渲染：去掉横向滚动、开启自动换行，外观保持库默认
        codeFence = { model ->
            MarkdownCodeFence(model.content, model.node) { code, _ ->
                WrappedCode(code, style = typography.code)
            }
        },
        codeBlock = { model ->
            MarkdownCodeBlock(model.content, model.node) { code, _ ->
                WrappedCode(code, style = typography.code)
            }
        },
        // 覆盖默认表格渲染：默认单元格是单行省略号截断，改为自动换行；
        // 列多到放不下时整表横向滚动
        table = { model ->
            WrappedTable(model.content, model.node, style = typography.text)
        }
    )

    SelectionContainer {
        Markdown(
            content = content,
            imageTransformer = Coil3ImageTransformerImpl,
            colors = colors,
            typography = typography,
            padding = padding,
            dimens = dimens,
            components = components,
        modifier = modifier
            .fillMaxSize()
            .pinchZoomText(onZoom = onZoom)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun WrappedCode(code: String, style: TextStyle) {
    val colors = LocalMarkdownColors.current
    val dimens = LocalMarkdownDimens.current
    val padding = LocalMarkdownPadding.current
    MarkdownCodeBackground(
        color = colors.codeBackground,
        shape = RoundedCornerShape(dimens.codeBackgroundCornerSize),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(
            text = code,
            color = colors.codeText,
            style = style,
            softWrap = true,
            modifier = Modifier.padding(padding.codeBlock)
        )
    }
}

@Composable
private fun WrappedTable(content: String, node: ASTNode, style: TextStyle) {
    val colors = LocalMarkdownColors.current
    val dimens = LocalMarkdownDimens.current
    val columns = remember(node) {
        node.findChildOfType(GFMElementTypes.HEADER)
            ?.children?.count { it.type == GFMTokenTypes.CELL } ?: 0
    }
    val minTableWidth = dimens.tableCellWidth * columns

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val scrollable = maxWidth < minTableWidth
        Column(
            modifier = (
                if (scrollable) {
                    Modifier.horizontalScroll(rememberScrollState()).requiredWidth(minTableWidth)
                } else {
                    Modifier.fillMaxWidth()
                }
                ).background(colors.tableBackground, RoundedCornerShape(dimens.tableCornerSize))
        ) {
            node.children.forEach { child ->
                when (child.type) {
                    GFMElementTypes.HEADER -> WrappedTableRow(content, child, style, isHeader = true)
                    GFMElementTypes.ROW -> WrappedTableRow(content, child, style, isHeader = false)
                    GFMTokenTypes.TABLE_SEPARATOR -> HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WrappedTableRow(content: String, row: ASTNode, style: TextStyle, isHeader: Boolean) {
    val colors = LocalMarkdownColors.current
    val cellPadding = LocalMarkdownDimens.current.tableCellPadding
    val cellStyle = if (isHeader) style.copy(fontWeight = FontWeight.SemiBold) else style
    Row(modifier = Modifier.fillMaxWidth()) {
        row.children.filter { it.type == GFMTokenTypes.CELL }.forEach { cell ->
            MarkdownBasicText(
                text = content.buildMarkdownAnnotatedString(cell, cellStyle),
                style = cellStyle,
                color = colors.tableText,
                modifier = Modifier
                    .weight(1f)
                    .padding(cellPadding)
            )
        }
    }
}
