package com.example.checkit_android.data

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String = java.util.UUID.randomUUID().toString(),
    var text: String,
    var isCompleted: Boolean = false
)