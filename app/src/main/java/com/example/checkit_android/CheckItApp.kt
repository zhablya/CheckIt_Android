package com.example.checkit_android

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.unit.sp

@Composable
fun CheckItApp() {
    val context = LocalContext.current
    val repository = remember { TaskRepository(context) }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Task?>(null) }

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TO-DO List",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 20.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { repository.exportTodos() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Экспорт в JSON")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { repository.importTodos() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Импорт из JSON")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp)
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