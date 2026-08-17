package com.example.dailytask.data.local

import com.example.dailytask.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskDao {
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun getDatesWithTasks(): Flow<List<String>>
    suspend fun getTaskById(id: Long): TaskEntity?
    suspend fun insertTask(task: TaskEntity): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteTaskById(id: Long)
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)
}
