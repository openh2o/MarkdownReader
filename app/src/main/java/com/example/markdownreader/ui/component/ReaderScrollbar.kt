package com.example.markdownreader.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val SCROLLBAR_EDGE_MARGIN = 8.dp
private val SCROLLBAR_THUMB_WIDTH = 4.dp
private val SCROLLBAR_HIT_WIDTH = 24.dp

/**
 * 右侧滚动位置指示条：滚动时淡入，停止约 1 秒后自动淡出；
 * 拖动或点击可直接跳转到文档对应位置。
 */
@Composable
fun ReaderScrollbar(
    scrollState: ScrollState,
    viewportHeightPx: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress to scrollState.value }
            .collectLatest { (isScrolling, _) ->
                if (scrollState.maxValue <= 0) {
                    visible = false
                    return@collectLatest
                }
                visible = true
                if (!isScrolling) {
                    delay(1000)
                    visible = false
                }
            }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(120)),
        exit = fadeOut(tween(250))
    ) {
        val thumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(SCROLLBAR_HIT_WIDTH)
                .pointerInput(scrollState) {
                    val seekTo: (Float) -> Unit = { y ->
                        val max = scrollState.maxValue
                        if (max > 0 && size.height > 0f) {
                            val marginPx = SCROLLBAR_EDGE_MARGIN.toPx()
                            val trackHeight = (size.height - marginPx * 2).coerceAtLeast(1f)
                            val fraction = ((y - marginPx) / trackHeight).coerceIn(0f, 1f)
                            val target = (fraction * max).roundToInt()
                            scope.launch { scrollState.scrollTo(target) }
                        }
                    }
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        seekTo(down.position.y)
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            if (!change.pressed) break
                            if (change.positionChange() != Offset.Zero) {
                                seekTo(change.position.y)
                                change.consume()
                            }
                        }
                    }
                }
        ) {
            if (scrollState.maxValue > 0 && viewportHeightPx > 0) {
                val marginPx = SCROLLBAR_EDGE_MARGIN.toPx()
                val thumbWidth = SCROLLBAR_THUMB_WIDTH.toPx()
                val trackHeight = size.height - marginPx * 2
                val contentHeight = (viewportHeightPx + scrollState.maxValue).toFloat()
                val thumbHeight = (trackHeight * viewportHeightPx / contentHeight)
                    .coerceAtLeast(24.dp.toPx())
                val positionFraction = scrollState.value.toFloat() / scrollState.maxValue
                val thumbY = marginPx + (trackHeight - thumbHeight) * positionFraction
                drawRoundRect(
                    color = thumbColor,
                    topLeft = Offset(size.width - thumbWidth - 4.dp.toPx(), thumbY),
                    size = Size(thumbWidth, thumbHeight),
                    cornerRadius = CornerRadius(thumbWidth / 2f)
                )
            }
        }
    }
}
