package com.example.markdownreader.ui.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * 双指捏合缩放手势：仅在检测到两指及以上时介入并消费事件，
 * 单指的上下滚动完全不受影响；捏合开始后会持续消费到手指全部抬起，
 * 避免中途剩余的单指被滚动容器接管造成跳动。
 */
fun Modifier.pinchZoomText(onZoom: (Float) -> Unit): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        var zooming = false
        while (true) {
            val event = awaitPointerEvent()
            val pressedCount = event.changes.count { it.pressed }
            if (pressedCount >= 2) {
                val zoom = event.calculateZoom()
                if (zoom != 1f) {
                    onZoom(zoom)
                    zooming = true
                }
            }
            if (zooming) {
                event.changes.forEach { change ->
                    if (change.pressed) change.consume()
                }
            }
            if (event.changes.none { it.pressed }) break
        }
    }
}
