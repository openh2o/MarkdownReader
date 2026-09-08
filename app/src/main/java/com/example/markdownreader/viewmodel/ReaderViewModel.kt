package com.example.markdownreader.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.markdownreader.data.FileReader
import com.example.markdownreader.data.MarkdownFile
import com.example.markdownreader.data.RecentFile
import com.example.markdownreader.data.ThemePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class ReaderUiState(
    val markdownFile: MarkdownFile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    /** 每次成功载入新文件时递增，用于区分“换了文件”和“同文件的模式切换副本” */
    val loadId: Long = 0
)

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application)

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState

    val isDarkMode: StateFlow<Boolean> = themePreferences.isDarkMode
        .map { it ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val recentFiles: StateFlow<List<RecentFile>> = themePreferences.recentFiles
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _fontScale = MutableStateFlow(DEFAULT_FONT_SCALE)
    val fontScale: StateFlow<Float> = _fontScale

    private var persistFontScaleJob: Job? = null

    init {
        viewModelScope.launch {
            themePreferences.fontScale.first()?.let { saved ->
                _fontScale.value = saved.coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
            }
        }
    }

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    private val _editContent = MutableStateFlow("")
    val editContent: StateFlow<String> = _editContent

    private var loadCounter = 0L

    fun loadFromUri(uri: Uri, contentResolver: ContentResolver) {
        val loadId = ++loadCounter
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ReaderUiState(isLoading = true, loadId = loadId)
            try {
                val file = FileReader.readMarkdownFromUri(uri, contentResolver)
                _uiState.value = ReaderUiState(markdownFile = file, loadId = loadId)
                _editContent.value = file.content
                themePreferences.addRecentFile(uri.toString(), file.name)
            } catch (e: Exception) {
                _uiState.value = ReaderUiState(
                    error = "无法读取文件: ${e.localizedMessage}"
                )
            }
        }
    }

    fun loadFromText(text: String) {
        _uiState.value = ReaderUiState(
            markdownFile = MarkdownFile(name = "分享的文本", content = text),
            loadId = ++loadCounter
        )
        _editContent.value = text
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            themePreferences.toggleDarkMode(!isDarkMode.value)
        }
    }

    fun toggleEditMode() {
        if (_isEditMode.value) {
            // 从编辑模式切换回阅览模式 → 用编辑后的内容更新渲染（保留 loadId，不算换文件）
            val currentFile = _uiState.value.markdownFile
            if (currentFile != null) {
                _uiState.value = ReaderUiState(
                    markdownFile = currentFile.copy(content = _editContent.value),
                    loadId = _uiState.value.loadId
                )
            }
        } else {
            // 从阅览模式切换到编辑模式 → 用当前内容初始化编辑器
            _editContent.value = _uiState.value.markdownFile?.content ?: ""
        }
        _isEditMode.value = !_isEditMode.value
    }

    fun updateEditContent(text: String) {
        _editContent.value = text
    }

    fun clearRecentFiles() {
        viewModelScope.launch {
            themePreferences.clearRecentFiles()
        }
    }

    /** 从错误页回到首页（最近打开列表） */
    fun dismissError() {
        _uiState.value = ReaderUiState()
    }

    /** 关闭当前文件，回到主页。编辑内容只存在于内存中，关闭即放弃（与切换文件的行为一致） */
    fun closeFile() {
        _uiState.value = ReaderUiState()
        _isEditMode.value = false
        _editContent.value = ""
    }

    fun setFontScale(scale: Float) {
        val clamped = scale.coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
        _fontScale.value = clamped
        // 缩放过程中每帧都会变化，去抖后再写入 DataStore
        persistFontScaleJob?.cancel()
        persistFontScaleJob = viewModelScope.launch {
            delay(FONT_SCALE_PERSIST_DELAY_MS)
            themePreferences.setFontScale(clamped)
        }
    }

    fun scaleFont(factor: Float) = setFontScale(_fontScale.value * factor)

    fun increaseFontScale() = setFontScale(roundToStep(_fontScale.value + FONT_SCALE_STEP))

    fun decreaseFontScale() = setFontScale(roundToStep(_fontScale.value - FONT_SCALE_STEP))

    fun resetFontScale() = setFontScale(DEFAULT_FONT_SCALE)

    private fun roundToStep(scale: Float): Float =
        (scale * 10).roundToInt() / 10f

    companion object {
        const val MIN_FONT_SCALE = 0.7f
        const val MAX_FONT_SCALE = 2.5f
        const val DEFAULT_FONT_SCALE = 1.0f
        const val FONT_SCALE_STEP = 0.1f
        private const val FONT_SCALE_PERSIST_DELAY_MS = 400L
    }
}
