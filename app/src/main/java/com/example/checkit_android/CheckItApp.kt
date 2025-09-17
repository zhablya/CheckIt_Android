package com.example.checkit_android

import android.content.Context
import android.net.Uri // 👈 ЭТО БЫЛО ПРОПУЩЕНО — главная причина ошибки!
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.checkit_android.components.AddEditDialog
import com.example.checkit_android.components.TaskItem
import com.example.checkit_android.data.Task
import com.example.checkit_android.data.TaskRepository
import com.example.checkit_android.utils.FileHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle

@Composable
fun CheckItApp() {
    val context = LocalContext.current as ComponentActivity
    val repository = remember { TaskRepository(context) }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Task?>(null) }

    // 📂 Импорт — выбор файла
    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val importedTodos = FileHelper.loadTodosFromUri(context, uri)
            if (importedTodos != null) {
                repository.todos.clear()
                repository.todos.addAll(importedTodos)
                FileHelper.saveTodos(context, importedTodos)
                Toast.makeText(context, "Список успешно импортирован", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Ошибка чтения файла", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 💾 Экспорт — сохранение в выбранное место
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            FileHelper.exportTodosToUri(context, uri, repository.todos.toList())
            Toast.makeText(context, "Список экспортирован", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(repository.todos) { todo ->
                    TaskItem(
                        task = todo,
                        onToggle = { repository.toggleTodo(it) },
                        onDelete = { repository.deleteTodo(it) },
                        onEdit = { editingTodo = it }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        importFileLauncher.launch(arrayOf("application/json"))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Импорт")
                }

                Spacer(Modifier.width(16.dp))

                OutlinedButton(
                    onClick = {
                        exportFileLauncher.launch(FileHelper.EXPORT_FILE_NAME)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Экспорт")
                }
            }

            if (showAddDialog) {
                AddEditDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { text -> repository.addTodo(text) }
                )
            }

            if (editingTodo != null) {
                AddEditDialog(
                    task = editingTodo,
                    onDismiss = { editingTodo = null },
                    onSave = { text -> editingTodo?.let { repository.updateTodo(it, text) } }
                )
            }
        }
    }
}