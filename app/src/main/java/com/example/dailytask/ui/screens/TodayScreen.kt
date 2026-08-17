package com.example.dailytask.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.data.model.CategoryConstants
import com.example.dailytask.ui.components.AddTaskBottomSheet
import com.example.dailytask.ui.components.ConfettiBurst
import com.example.dailytask.ui.components.ExpandableCalendarHeader
import com.example.dailytask.ui.components.LottieEmptyState
import com.example.dailytask.ui.components.SettingsBottomSheet
import com.example.dailytask.ui.components.TaskItemCard
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.HeaderDarkEnd
import com.example.dailytask.ui.theme.HeaderDarkStart
import com.example.dailytask.ui.viewmodel.DailyTaskViewModel
import java.util.Calendar

private fun getHumanNarrative(totalCount: Int, completedCount: Int): String = when {
    totalCount == 0 -> "Belum ada aktivitas tercatat. Apa yang sudah kamu lakukan hari ini?"
    totalCount == 1 -> "1 aktivitas tercatat untuk hari ini."
    else -> "Ada $totalCount aktivitas tercatat hari ini."
}

private fun getTimeGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 5  -> "Selamat Dini Hari"
        hour < 12 -> "Selamat Pagi"
        hour < 15 -> "Selamat Siang"
        hour < 18 -> "Selamat Sore"
        else -> "Selamat Malam"
    }
}

@Composable
fun TodayScreen(
    viewModel: DailyTaskViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.selectedDateTasks.collectAsState()
    val datesWithTasks by viewModel.datesWithTasks.collectAsState()
    val inputText by viewModel.todayInputText.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val year by viewModel.calendarYear.collectAsState()
    val month by viewModel.calendarMonth.collectAsState()

    var isCalendarExpanded by remember { mutableStateOf(false) }
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var selectedQuickCategory by remember { mutableStateOf("Umum") }

    val focusManager = LocalFocusManager.current

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val targetProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progressAnimation"
    )

    val greeting = getTimeGreeting()
    val narrativeQuote = getHumanNarrative(totalCount, completedCount)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            // ── Expandable Obsidian Calendar Header ───────────────────
            item(key = "calendar_header") {
                ExpandableCalendarHeader(
                    userName = userName,
                    greeting = greeting,
                    motivationalQuote = narrativeQuote,
                    totalCount = totalCount,
                    isAllDone = false,
                    animatedProgress = animatedProgress,
                    selectedDate = selectedDate,
                    datesWithTasks = datesWithTasks,
                    year = year,
                    month = month,
                    isExpanded = isCalendarExpanded,
                    onToggleExpand = { isCalendarExpanded = !isCalendarExpanded },
                    onSelectDate = { viewModel.selectDate(it) },
                    onChangeMonth = { viewModel.changeCalendarMonth(it) },
                    onResetToday = { viewModel.resetCalendarToToday() },
                    onOpenSettings = { showSettingsSheet = true }
                )
            }

            // ── Section Title (Clean & Minimalist) ─────────────────────
            item(key = "agenda_heading") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aktivitas Hari Ini",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (totalCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 0.8.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentMint)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$totalCount aktivitas",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // ── Activity Timeline List or Empty State ─────────────────
            if (tasks.isEmpty()) {
                item(key = "empty_placeholder") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieEmptyState()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada catatan aktivitas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = (-0.2).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pilih kategori dan ketik aktivitas yang baru kamu lakukan di bawah",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = tasks,
                    key = { _, it -> it.id }
                ) { index, task ->
                    TaskItemCard(
                        task = task,
                        isFirst = index == 0,
                        isLast = index == tasks.size - 1,
                        onDelete = { viewModel.deleteTask(task.id) },
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )
                }
            }
        }

        // ── Floating iOS Bottom Bar with Category Selector Above Input ──
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            shadowElevation = 18.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 0.8.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp)
            ) {
                // ── Category Selector Pills (Above Input) ──
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp)
                ) {
                    items(CategoryConstants.categories, key = { it.name }) { cat ->
                        val isSelected = selectedQuickCategory == cat.name
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable { selectedQuickCategory = cat.name },
                            shape = RoundedCornerShape(50.dp),
                            color = if (isSelected) cat.primaryColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                width = 0.8.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else cat.primaryColor)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.5.sp
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Quick Add Input Row ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.onTodayInputChange(it) },
                        placeholder = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Catat aktivitas [$selectedQuickCategory]...",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (inputText.isNotBlank()) {
                                    viewModel.addQuickTask(selectedQuickCategory)
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )

                    // Quick Add Action Pill
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (inputText.isNotBlank())
                                    Brush.linearGradient(listOf(HeaderDarkStart, HeaderDarkEnd))
                                else
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                            )
                            .clickable(enabled = inputText.isNotBlank()) {
                                viewModel.addQuickTask(selectedQuickCategory)
                                focusManager.clearFocus()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Catat",
                            tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Modal Schedule FAB
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 0.8.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { showAddTaskSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "Catat Detail",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        // ── Bottom Sheets ─────────────────────────────────────────────
        if (showAddTaskSheet) {
            AddTaskBottomSheet(
                initialDate = selectedDate,
                onDismiss = { showAddTaskSheet = false },
                onSave = { task -> viewModel.addTask(task) }
            )
        }

        if (showSettingsSheet) {
            SettingsBottomSheet(
                currentTheme = themeMode,
                currentUserName = userName,
                notificationsEnabled = notificationsEnabled,
                onThemeChange = { viewModel.setThemeMode(it) },
                onUserNameChange = { viewModel.setUserName(it) },
                onNotificationsChange = { viewModel.setNotificationsEnabled(it) },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
