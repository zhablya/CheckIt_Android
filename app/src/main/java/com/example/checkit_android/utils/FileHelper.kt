package com.example.checkit_android.utils

import android.content.Context
import com.example.checkit_android.data.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object FileHelper {
    private const val FILE_NAME = "tasks.json"

    fun getTodoFile(context: Context): File {
        return File(context.getExternalFilesDir(null), FILE_NAME)
    }

    fun saveTodos(context: Context, todos: List<Task>) {
        val json = Json.encodeToString(todos)
        getTodoFile(context).writeText(json)
    }

    fun loadTodos(context: Context): List<Task> {
        val file = getTodoFile(context)
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString<List<Task>>(json)
        } else {
            emptyList()
        }
    }
}