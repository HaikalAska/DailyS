package com.example.dailytask.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dailytask.data.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppDatabase private constructor(context: Context) {

    private val dbHelper = TaskDbHelper(context.applicationContext)
    private val changeNotifier = MutableStateFlow(System.currentTimeMillis())

    val taskDao: TaskDao = object : TaskDao {

        override fun getTasksByDate(date: String): Flow<List<TaskEntity>> {
            return changeNotifier.map {
                withContext(Dispatchers.IO) {
                    queryTasksByDate(date)
                }
            }
        }

        override fun getAllTasks(): Flow<List<TaskEntity>> {
            return changeNotifier.map {
                withContext(Dispatchers.IO) {
                    queryAllTasks()
                }
            }
        }

        override fun getDatesWithTasks(): Flow<List<String>> {
            return changeNotifier.map {
                withContext(Dispatchers.IO) {
                    queryDatesWithTasks()
                }
            }
        }

        override suspend fun getTaskById(id: Long): TaskEntity? = withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                TaskDbHelper.TABLE_TASKS,
                null,
                "${TaskDbHelper.COLUMN_ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
            cursor.use {
                if (it.moveToFirst()) {
                    readTaskFromCursor(it)
                } else null
            }
        }

        override suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(TaskDbHelper.COLUMN_TITLE, task.title)
                put(TaskDbHelper.COLUMN_DESCRIPTION, task.description)
                put(TaskDbHelper.COLUMN_DATE, task.date)
                put(TaskDbHelper.COLUMN_TIME, task.time)
                put(TaskDbHelper.COLUMN_TIMESTAMP, task.timestamp)
                put(TaskDbHelper.COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
                put(TaskDbHelper.COLUMN_CATEGORY, task.category)
                put(TaskDbHelper.COLUMN_PRIORITY, task.priority)
                put(TaskDbHelper.COLUMN_HAS_REMINDER, if (task.hasReminder) 1 else 0)
            }
            val id = db.insert(TaskDbHelper.TABLE_TASKS, null, values)
            notifyChange()
            id
        }

        override suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(TaskDbHelper.COLUMN_TITLE, task.title)
                put(TaskDbHelper.COLUMN_DESCRIPTION, task.description)
                put(TaskDbHelper.COLUMN_DATE, task.date)
                put(TaskDbHelper.COLUMN_TIME, task.time)
                put(TaskDbHelper.COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
                put(TaskDbHelper.COLUMN_CATEGORY, task.category)
                put(TaskDbHelper.COLUMN_PRIORITY, task.priority)
                put(TaskDbHelper.COLUMN_HAS_REMINDER, if (task.hasReminder) 1 else 0)
            }
            db.update(
                TaskDbHelper.TABLE_TASKS,
                values,
                "${TaskDbHelper.COLUMN_ID} = ?",
                arrayOf(task.id.toString())
            )
            notifyChange()
        }

        override suspend fun deleteTask(task: TaskEntity) {
            deleteTaskById(task.id)
        }

        override suspend fun deleteTaskById(id: Long) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete(
                TaskDbHelper.TABLE_TASKS,
                "${TaskDbHelper.COLUMN_ID} = ?",
                arrayOf(id.toString())
            )
            notifyChange()
        }

        override suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(TaskDbHelper.COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
            }
            db.update(
                TaskDbHelper.TABLE_TASKS,
                values,
                "${TaskDbHelper.COLUMN_ID} = ?",
                arrayOf(id.toString())
            )
            notifyChange()
        }
    }

    fun taskDao(): TaskDao = taskDao

    private fun notifyChange() {
        changeNotifier.value = System.currentTimeMillis()
    }

    private fun queryTasksByDate(date: String): List<TaskEntity> {
        val list = mutableListOf<TaskEntity>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TaskDbHelper.TABLE_TASKS,
            null,
            "${TaskDbHelper.COLUMN_DATE} = ?",
            arrayOf(date),
            null,
            null,
            "${TaskDbHelper.COLUMN_IS_COMPLETED} ASC, ${TaskDbHelper.COLUMN_PRIORITY} DESC, ${TaskDbHelper.COLUMN_TIMESTAMP} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(readTaskFromCursor(it))
            }
        }
        return list
    }

    private fun queryAllTasks(): List<TaskEntity> {
        val list = mutableListOf<TaskEntity>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TaskDbHelper.TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "${TaskDbHelper.COLUMN_DATE} DESC, ${TaskDbHelper.COLUMN_TIMESTAMP} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(readTaskFromCursor(it))
            }
        }
        return list
    }

    private fun queryDatesWithTasks(): List<String> {
        val list = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            true,
            TaskDbHelper.TABLE_TASKS,
            arrayOf(TaskDbHelper.COLUMN_DATE),
            null,
            null,
            null,
            null,
            null,
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.getString(it.getColumnIndexOrThrow(TaskDbHelper.COLUMN_DATE)))
            }
        }
        return list
    }

    private fun readTaskFromCursor(cursor: android.database.Cursor): TaskEntity {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TITLE))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_DESCRIPTION)) ?: ""
        val date = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_DATE))
        val time = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TIME)) ?: ""
        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TIMESTAMP))
        val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_IS_COMPLETED)) == 1
        val category = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_CATEGORY)) ?: "Umum"
        val priority = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_PRIORITY)) ?: "Normal"
        val hasReminder = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_HAS_REMINDER)) == 1

        return TaskEntity(
            id = id,
            title = title,
            description = description,
            date = date,
            time = time,
            timestamp = timestamp,
            isCompleted = isCompleted,
            category = category,
            priority = priority,
            hasReminder = hasReminder
        )
    }

    private class TaskDbHelper(context: Context) : SQLiteOpenHelper(context, "dailytask_app.db", null, 1) {
        companion object {
            const val TABLE_TASKS = "tasks"
            const val COLUMN_ID = "id"
            const val COLUMN_TITLE = "title"
            const val COLUMN_DESCRIPTION = "description"
            const val COLUMN_DATE = "date"
            const val COLUMN_TIME = "time"
            const val COLUMN_TIMESTAMP = "timestamp"
            const val COLUMN_IS_COMPLETED = "is_completed"
            const val COLUMN_CATEGORY = "category"
            const val COLUMN_PRIORITY = "priority"
            const val COLUMN_HAS_REMINDER = "has_reminder"
        }

        override fun onCreate(db: SQLiteDatabase) {
            val query = """
                CREATE TABLE $TABLE_TASKS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_TITLE TEXT NOT NULL,
                    $COLUMN_DESCRIPTION TEXT,
                    $COLUMN_DATE TEXT NOT NULL,
                    $COLUMN_TIME TEXT,
                    $COLUMN_TIMESTAMP INTEGER NOT NULL,
                    $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                    $COLUMN_CATEGORY TEXT,
                    $COLUMN_PRIORITY TEXT,
                    $COLUMN_HAS_REMINDER INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent()
            db.execSQL(query)
            db.execSQL("CREATE INDEX idx_tasks_date ON $TABLE_TASKS ($COLUMN_DATE)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
            onCreate(db)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
