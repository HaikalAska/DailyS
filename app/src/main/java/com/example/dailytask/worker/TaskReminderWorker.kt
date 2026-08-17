package com.example.dailytask.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dailytask.MainActivity

class TaskReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "daily_task_reminders"
        const val CHANNEL_NAME = "Pengingat Jadwal Harian"
        const val KEY_TASK_ID = "task_id"
        const val KEY_TASK_TITLE = "task_title"
        const val KEY_TASK_TIME = "task_time"
        const val KEY_TASK_CATEGORY = "task_category"
    }

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, 0L)
        val taskTitle = inputData.getString(KEY_TASK_TITLE) ?: "Waktunya menyelesaikan jadwal!"
        val taskTime = inputData.getString(KEY_TASK_TIME) ?: ""
        val taskCategory = inputData.getString(KEY_TASK_CATEGORY) ?: "Jadwal"

        showNotification(taskId, taskTitle, taskTime, taskCategory)
        return Result.success()
    }

    private fun showNotification(
        taskId: Long,
        title: String,
        time: String,
        category: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat untuk jadwal dan tugas harian"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ $category: $title")
            .setContentText(if (time.isNotEmpty()) "Jadwal pada jam $time • Tetap semangat!" else "Saatnya menyelesaikan aktivitas ini!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }
}
