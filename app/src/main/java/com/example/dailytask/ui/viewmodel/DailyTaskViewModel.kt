package com.example.dailytask.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytask.data.model.TaskEntity
import com.example.dailytask.data.preferences.UserPreferencesRepository
import com.example.dailytask.data.repository.TaskRepository
import com.example.dailytask.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class NavTab {
    TODAY,
    CALENDAR
}

@OptIn(ExperimentalCoroutinesApi::class)
class DailyTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    // DataStore Preferences
    val themeMode: StateFlow<String> = preferencesRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val userName: StateFlow<String> = preferencesRepository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sobat Produktif")

    val notificationsEnabled: StateFlow<Boolean> = preferencesRepository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // UI Navigation & Dates
    private val _currentTab = MutableStateFlow(NavTab.TODAY)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _todayInputText = MutableStateFlow("")
    val todayInputText: StateFlow<String> = _todayInputText.asStateFlow()

    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Calendar month tracking
    private val currentCal = Calendar.getInstance()
    private val _calendarYear = MutableStateFlow(currentCal.get(Calendar.YEAR))
    val calendarYear: StateFlow<Int> = _calendarYear.asStateFlow()

    private val _calendarMonth = MutableStateFlow(currentCal.get(Calendar.MONTH))
    val calendarMonth: StateFlow<Int> = _calendarMonth.asStateFlow()

    // Reactive Room queries
    val datesWithTasks: StateFlow<Set<String>> = taskRepository.getDatesWithTasks()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Filtered tasks for the currently selected date
    val selectedDateTasks: StateFlow<List<TaskEntity>> = _selectedDate
        .flatMapLatest { date ->
            taskRepository.getTasksForDate(date)
        }
        .combine(_filterCategory) { tasks, category ->
            if (category == null) tasks else tasks.filter { it.category.equals(category, ignoreCase = true) }
        }
        .combine(_searchQuery) { tasks, query ->
            if (query.isBlank()) tasks else tasks.filter {
                it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun switchTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun onTodayInputChange(newText: String) {
        _todayInputText.value = newText
    }

    fun setFilterCategory(category: String?) {
        _filterCategory.value = if (_filterCategory.value == category) null else category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addQuickTask(category: String = "Umum") {
        val text = _todayInputText.value.trim()
        if (text.isEmpty()) return

        val newTask = TaskEntity(
            title = text,
            date = _selectedDate.value,
            time = DateUtils.getCurrentTimeString(),
            category = category,
            priority = "Normal",
            hasReminder = false,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            taskRepository.addTask(newTask)
            _todayInputText.value = ""
        }
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.addTask(task)
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }

    fun changeCalendarMonth(offset: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _calendarYear.value)
            set(Calendar.MONTH, _calendarMonth.value)
            set(Calendar.DAY_OF_MONTH, 1) // Set to 1st to prevent skipping months (e.g. Aug 31 + 1 month = Oct 1)
            add(Calendar.MONTH, offset)
        }
        _calendarYear.value = cal.get(Calendar.YEAR)
        _calendarMonth.value = cal.get(Calendar.MONTH)
    }

    fun resetCalendarToToday() {
        val todayCal = Calendar.getInstance()
        _calendarYear.value = todayCal.get(Calendar.YEAR)
        _calendarMonth.value = todayCal.get(Calendar.MONTH)
        selectDate(DateUtils.getTodayDateString())
    }

    // DataStore settings
    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setUserName(name)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }
}
