package com.example.markdownreader.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedReader
import java.io.InputStreamReader

object FileReader {

    fun readMarkdownFromUri(uri: Uri, contentResolver: ContentResolver): MarkdownFile {
        val name = getFileName(uri, contentResolver)
        val content = when (uri.scheme) {
            "content" -> readContentUri(uri, contentResolver)
            "file" -> uri.path?.let { java.io.File(it).readText(Charsets.UTF_8) } ?: ""
            else -> readContentUri(uri, contentResolver)
        }
        return MarkdownFile(name = name, content = content)
    }

    private fun readContentUri(uri: Uri, contentResolver: ContentResolver): String {
        return contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
        } ?: ""
    }

    private fun getFileName(uri: Uri, contentResolver: ContentResolver): String {
        if (uri.scheme == "content") {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            return cursor.getString(nameIndex)
                        }
                    }
                }
        }
        return uri.lastPathSegment?.substringAfterLast('/') ?: "Untitled.md"
    }
}
