package com.example.dailytask.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailytask.data.model.TaskEntity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleTaskReminder(context: Context, task: TaskEntity) {
        if (!task.hasReminder || task.time.isEmpty() || task.date.isEmpty()) return

        val workManager = WorkManager.getInstance(context)
        val tag = "reminder_${task.id}"

        // Parse target timestamp
        val dateTimeString = "${task.date} ${task.time}"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val targetDate = try {
            sdf.parse(dateTimeString)
        } catch (e: Exception) {
            null
        } ?: return

        val currentTime = System.currentTimeMillis()
        val delayMillis = targetDate.time - currentTime

        // Only schedule if it's in the future
        if (delayMillis > 0) {
            val inputData = Data.Builder()
                .putLong(TaskReminderWorker.KEY_TASK_ID, task.id)
                .putString(TaskReminderWorker.KEY_TASK_TITLE, task.title)
                .putString(TaskReminderWorker.KEY_TASK_TIME, task.time)
                .putString(TaskReminderWorker.KEY_TASK_CATEGORY, task.category)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(tag)
                .build()

            workManager.enqueueUniqueWork(
                tag,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun cancelTaskReminder(context: Context, taskId: Long) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("reminder_$taskId")
    }
}
