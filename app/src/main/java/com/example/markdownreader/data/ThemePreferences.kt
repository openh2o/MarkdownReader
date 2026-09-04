package com.example.markdownreader.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val FONT_SCALE_KEY = floatPreferencesKey("font_scale")
        private val RECENT_FILES_KEY = stringPreferencesKey("recent_files")
        private const val MAX_RECENT_FILES = 20

        private fun parseRecentFiles(json: String): List<RecentFile> = try {
            val array = JSONArray(json)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                RecentFile(
                    uri = item.getString("uri"),
                    name = item.getString("name"),
                    lastOpened = item.optLong("time")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }

        private fun encodeRecentFiles(files: List<RecentFile>): String {
            val array = JSONArray()
            files.forEach { file ->
                array.put(
                    JSONObject()
                        .put("uri", file.uri)
                        .put("name", file.name)
                        .put("time", file.lastOpened)
                )
            }
            return array.toString()
        }
    }

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY]
    }

    val fontScale: Flow<Float?> = context.dataStore.data.map { preferences ->
        preferences[FONT_SCALE_KEY]
    }

    val recentFiles: Flow<List<RecentFile>> = context.dataStore.data.map { preferences ->
        preferences[RECENT_FILES_KEY]?.let { parseRecentFiles(it) } ?: emptyList()
    }

    suspend fun toggleDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SCALE_KEY] = scale
        }
    }

    /** 记录一次打开：去重后置于列表最前，最多保留 MAX_RECENT_FILES 条 */
    suspend fun addRecentFile(uri: String, name: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[RECENT_FILES_KEY]?.let { parseRecentFiles(it) } ?: emptyList()
            val updated = listOf(RecentFile(uri, name, System.currentTimeMillis())) +
                    current.filterNot { it.uri == uri }
            preferences[RECENT_FILES_KEY] = encodeRecentFiles(updated.take(MAX_RECENT_FILES))
        }
    }

    suspend fun clearRecentFiles() {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_FILES_KEY)
        }
    }
}
