package com.example.dailytask.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dailytask.data.model.TaskItem

class DailyTaskDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "daily_tasks.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_CATEGORY = "category"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CATEGORY TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        val createIndexQuery = "CREATE INDEX idx_tasks_date ON $TABLE_TASKS ($COLUMN_DATE)"
        db.execSQL(createIndexQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertTask(task: TaskItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DATE, task.date)
            put(COLUMN_TIMESTAMP, task.timestamp)
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(COLUMN_CATEGORY, task.category)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun updateTask(task: TaskItem): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DATE, task.date)
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(COLUMN_CATEGORY, task.category)
        }
        return db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
    }

    fun toggleTaskCompletion(id: Long, isCompleted: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        return db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getTasksByDate(date: String): List<TaskItem> {
        val taskList = mutableListOf<TaskItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null,
            null,
            "$COLUMN_TIMESTAMP ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                val description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: ""
                val taskDate = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                val isCompleted = it.getInt(it.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
                val category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)) ?: ""

                taskList.add(
                    TaskItem(
                        id = id,
                        title = title,
                        description = description,
                        date = taskDate,
                        timestamp = timestamp,
                        isCompleted = isCompleted,
                        category = category
                    )
                )
            }
        }
        return taskList
    }

    fun getAllTasks(): List<TaskItem> {
        val taskList = mutableListOf<TaskItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE DESC, $COLUMN_TIMESTAMP DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                val description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: ""
                val taskDate = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                val isCompleted = it.getInt(it.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
                val category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)) ?: ""

                taskList.add(
                    TaskItem(
                        id = id,
                        title = title,
                        description = description,
                        date = taskDate,
                        timestamp = timestamp,
                        isCompleted = isCompleted,
                        category = category
                    )
                )
            }
        }
        return taskList
    }

    fun getDatesWithTasks(): Set<String> {
        val dates = mutableSetOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            true, // DISTINCT
            TABLE_TASKS,
            arrayOf(COLUMN_DATE),
            null,
            null,
            null,
            null,
            null,
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE))
                dates.add(date)
            }
        }
        return dates
    }
}
