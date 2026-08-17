package com.example.dailytask.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TaskEntity(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // Format "yyyy-MM-dd"
    val time: String = "", // Format "HH:mm"
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val category: String = "Umum", // Umum, Kerja, Belajar, Pribadi, Kesehatan, Penting
    val priority: String = "Normal", // Rendah, Normal, Tinggi
    val hasReminder: Boolean = false
) {
    val displayTime: String
        get() = if (time.isNotEmpty()) {
            time
        } else {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
}
