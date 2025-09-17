package com.example.checkit_android.utils

import android.content.Context
import android.net.Uri
import android.content.ContentResolver
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import com.example.checkit_android.data.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object FileHelper {

    private const val PRIVATE_FILE_NAME = "todos.json"
    const val EXPORT_FILE_NAME = "todo_list.json"

    // Приватный файл приложения (для автосохранения)
    fun getPrivateTodoFile(context: Context): File {
        return File(context.getExternalFilesDir(null), PRIVATE_FILE_NAME)
    }

    // 👇 Сохранение в приватный файл (используется при каждом изменении)
    fun saveTodos(context: Context, todos: List<Task>) {
        val json = Json.encodeToString(todos)
        getPrivateTodoFile(context).writeText(json)
    }

    // 👇 Загрузка из приватного файла (при старте)
    fun loadTodos(context: Context): List<Task> {
        val file = getPrivateTodoFile(context)
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString<List<Task>>(json)
        } else {
            emptyList()
        }
    }

    // 👇 Загрузка из выбранного Uri (импорт)
    fun loadTodosFromUri(context: Context, uri: Uri): List<Task>? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream = contentResolver.openInputStream(uri)!!
            val json = inputStream.bufferedReader().use { it.readText() }
            Json.decodeFromString<List<Task>>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 👇 Экспорт: запись списка дел в выбранный Uri (например, в Downloads)
    fun exportTodosToUri(context: Context, uri: Uri, todos: List<Task>) {
        try {
            val json = Json.encodeToString(todos)
            val contentResolver: ContentResolver = context.contentResolver
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 👇 Создаём Uri для сохранения в папку Downloads по умолчанию (для ACTION_CREATE_DOCUMENT)
    fun createDefaultExportUri(context: Context): Uri? {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, EXPORT_FILE_NAME)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}