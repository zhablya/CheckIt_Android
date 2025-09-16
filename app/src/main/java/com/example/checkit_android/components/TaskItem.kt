package com.example.checkit_android.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import com.example.checkit_android.data.Task


@Composable
fun TaskItem(
    task: Task,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle(task) }
        )

        Text(
            text = task.text,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            style = if (task.isCompleted) MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
            else MaterialTheme.typography.bodyMedium
        )

        IconButton(onClick = { onEdit(task) }) {
            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
        }

        IconButton(onClick = { onDelete(task) }) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить")
        }
    }
}