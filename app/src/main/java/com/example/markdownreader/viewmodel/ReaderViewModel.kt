package com.example.markdownreader.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.markdownreader.data.FileReader
import com.example.markdownreader.data.MarkdownFile
import com.example.markdownreader.data.ThemePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReaderUiState(
    val markdownFile: MarkdownFile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application)

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState

    val isDarkMode: StateFlow<Boolean> = themePreferences.isDarkMode
        .map { it ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    private val _editContent = MutableStateFlow("")
    val editContent: StateFlow<String> = _editContent

    fun loadFromUri(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ReaderUiState(isLoading = true)
            try {
                val file = FileReader.readMarkdownFromUri(uri, contentResolver)
                _uiState.value = ReaderUiState(markdownFile = file)
                _editContent.value = file.content
            } catch (e: Exception) {
                _uiState.value = ReaderUiState(
                    error = "无法读取文件: ${e.localizedMessage}"
                )
            }
        }
    }

    fun loadFromText(text: String) {
        _uiState.value = ReaderUiState(
            markdownFile = MarkdownFile(name = "分享的文本", content = text)
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
            // 从编辑模式切换回阅览模式 → 用编辑后的内容更新渲染
            val currentFile = _uiState.value.markdownFile
            if (currentFile != null) {
                _uiState.value = ReaderUiState(
                    markdownFile = currentFile.copy(content = _editContent.value)
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
}
