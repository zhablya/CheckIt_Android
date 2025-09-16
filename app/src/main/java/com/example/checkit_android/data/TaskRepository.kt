package com.example.checkit_android.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import android.content.Context
import com.example.checkit_android.utils.FileHelper

class TaskRepository(private val context: android.content.Context) {

    private val _todos = mutableStateListOf<Task>()
    val todos: SnapshotStateList<Task> = _todos

    fun addTodo(text: String) {
        if (text.isNotBlank()) {
            _todos.add(Task(text = text.trim()))
        }
    }

    fun deleteTodo(todo: Task) {
        _todos.remove(todo)
    }

    fun toggleTodo(oldTodo: Task) {
        val index = todos.indexOfFirst { it.id == oldTodo.id }
        if (index != -1) {
            val updatedTodo = oldTodo.copy(isCompleted = !oldTodo.isCompleted)
            todos[index] = updatedTodo
        }
    }

    fun updateTodo(oldTodo: Task, newText: String) {
        if (newText.isNotBlank()) {
            val index = todos.indexOfFirst { it.id == oldTodo.id }
            if (index != -1) {

                val updatedTodo = oldTodo.copy(text = newText.trim())
                todos[index] = updatedTodo
            }
        }
    }

    fun exportTodos() {
        FileHelper.saveTodos(context, todos.toList())
    }

    fun importTodos() {
        val loaded = FileHelper.loadTodos(context)
        _todos.clear()
        _todos.addAll(loaded)
    }
}