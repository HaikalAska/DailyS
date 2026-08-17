package com.example.dailytask.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TaskItem(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val category: String = ""
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
