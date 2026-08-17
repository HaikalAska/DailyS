package com.example.dailytask.data.repository

import android.content.Context
import com.example.dailytask.data.local.AppDatabase
import com.example.dailytask.data.model.TaskEntity
import com.example.dailytask.worker.ReminderScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val db = AppDatabase.getDatabase(context)
    private val taskDao = db.taskDao()

    fun getTasksForDate(date: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDate(date)
    }

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    fun getDatesWithTasks(): Flow<List<String>> {
        return taskDao.getDatesWithTasks()
    }

    suspend fun getTaskById(id: Long): TaskEntity? = withContext(ioDispatcher) {
        taskDao.getTaskById(id)
    }

    suspend fun addTask(task: TaskEntity): Long = withContext(ioDispatcher) {
        val id = taskDao.insertTask(task)
        if (task.hasReminder) {
            val savedTask = task.copy(id = id)
            ReminderScheduler.scheduleTaskReminder(context, savedTask)
        }
        id
    }

    suspend fun updateTask(task: TaskEntity) = withContext(ioDispatcher) {
        taskDao.updateTask(task)
        if (task.hasReminder) {
            ReminderScheduler.scheduleTaskReminder(context, task)
        } else {
            ReminderScheduler.cancelTaskReminder(context, task.id)
        }
    }

    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) = withContext(ioDispatcher) {
        taskDao.updateTaskCompletion(id, isCompleted)
    }

    suspend fun deleteTask(id: Long) = withContext(ioDispatcher) {
        ReminderScheduler.cancelTaskReminder(context, id)
        taskDao.deleteTaskById(id)
    }
}
