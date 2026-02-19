package com.example.markdownreader.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.markdownDimens
import com.mikepenz.markdown.model.markdownPadding

@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier
) {
    val colors = markdownColor(
        dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )

    val typography = markdownTypography(
        h1 = TextStyle(
            fontSize = 26.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        ),
        h2 = TextStyle(
            fontSize = 22.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        ),
        h3 = TextStyle(
            fontSize = 19.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        ),
        h4 = TextStyle(
            fontSize = 17.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        h5 = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        h6 = TextStyle(
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        text = TextStyle(
            fontSize = 16.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp
        ),
        paragraph = TextStyle(
            fontSize = 16.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp
        ),
        quote = TextStyle(
            fontSize = 15.sp,
            lineHeight = 26.sp,
            fontStyle = FontStyle.Italic,
            letterSpacing = 0.25.sp
        ),
        code = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontFamily = FontFamily.Monospace
        ),
        bullet = TextStyle(
            fontSize = 16.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.25.sp
        ),
        ordered = TextStyle(
            fontSize = 16.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.25.sp
        ),
        list = TextStyle(
            fontSize = 16.sp,
            lineHeight = 28.sp,
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
        }
    )

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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    )
}
