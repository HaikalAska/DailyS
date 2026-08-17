package com.example.dailytask.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.dailytask.ui.components.HorizontalWeekStrip
import com.example.dailytask.ui.components.LottieEmptyState
import com.example.dailytask.ui.components.SettingsBottomSheet
import com.example.dailytask.ui.components.TaskItemCard
import com.example.dailytask.ui.theme.AccentAmber
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.CoralGradientEnd
import com.example.dailytask.ui.theme.CoralGradientStart
import com.example.dailytask.ui.theme.VioletGradientEnd
import com.example.dailytask.ui.theme.VioletGradientStart
import com.example.dailytask.ui.viewmodel.DailyTaskViewModel
import com.example.dailytask.util.DateUtils
import java.util.Calendar

private fun getMotivationalQuote(completedRatio: Float): String = when {
    completedRatio >= 1f  -> "Luar biasa! Semua selesai! 🎉"
    completedRatio >= 0.7f -> "Hampir selesai, terus semangat! 💪"
    completedRatio >= 0.4f -> "Kamu sudah di jalur yang benar! 🚀"
    else -> "Hari baru, energi baru. Ayo mulai! ✨"
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

    val focusManager = LocalFocusManager.current

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val targetProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 900),
        label = "progressAnimation"
    )

    val isAllDone = totalCount > 0 && completedCount == totalCount
    val greeting = getTimeGreeting()
    val motivationalQuote = getMotivationalQuote(targetProgress)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ── Expandable Gradient Calendar Header ───────────────────
            item(key = "calendar_header") {
                ExpandableCalendarHeader(
                    userName = userName,
                    greeting = greeting,
                    motivationalQuote = motivationalQuote,
                    totalCount = totalCount,
                    isAllDone = isAllDone,
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


            // ── Stats bar ────────────────────────────────────────────
            if (totalCount > 0) {
                item(key = "stats_summary") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatChip(
                            label = "Total",
                            value = "$totalCount",
                            color = CoralGradientStart,
                            containerColor = CoralGradientStart.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Selesai",
                            value = "$completedCount",
                            color = AccentMint,
                            containerColor = AccentMint.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Tersisa",
                            value = "${totalCount - completedCount}",
                            color = AccentAmber,
                            containerColor = AccentAmber.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Category filter chips ─────────────────────────────────
            item(key = "category_filters") {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    item(key = "cat_all") {
                        val isAllSelected = filterCategory == null
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable { viewModel.setFilterCategory(null) },
                            shape = RoundedCornerShape(50.dp),
                            color = if (isAllSelected) CoralGradientStart else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isAllSelected) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = if (isAllSelected) "Semua  ${tasks.size}" else "Semua",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(CategoryConstants.categories, key = { it.name }) { cat ->
                        val isSelected = filterCategory == cat.name
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable { viewModel.setFilterCategory(cat.name) },
                            shape = RoundedCornerShape(50.dp),
                            color = if (isSelected) cat.primaryColor else cat.containerColor.copy(alpha = 0.55f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = if (isSelected) Color.White else cat.primaryColor
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else cat.primaryColor
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // ── All done celebration banner ───────────────────────────
            if (isAllDone) {
                item(key = "celebration_banner") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = AccentMint.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = AccentMint,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Semua Selesai! 🎉",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = AccentMint
                                )
                                Text(
                                    text = "Kamu berhasil menyelesaikan $totalCount aktivitas hari ini!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // ── Task list or empty state ──────────────────────────────
            if (tasks.isEmpty()) {
                item(key = "empty_placeholder") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieEmptyState()
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Belum ada catatan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tulis aktivitas di bawah atau tekan + untuk jadwal lengkap",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    TaskItemCard(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }


        // ── Confetti overlay ─────────────────────────────────────────
        if (isAllDone) {
            ConfettiBurst(modifier = Modifier.fillMaxSize())
        }

        // ── Floating quick-add + FAB bar ─────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
            shadowElevation = 20.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
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
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Apa yang sudah kamu lakukan?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoralGradientStart,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        focusedContainerColor = CoralGradientStart.copy(alpha = 0.04f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (inputText.isNotBlank()) {
                                viewModel.addQuickTask()
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Quick add button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (inputText.isNotBlank())
                                Brush.linearGradient(listOf(CoralGradientStart, CoralGradientEnd))
                            else
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                        )
                        .clickable(enabled = inputText.isNotBlank()) {
                            viewModel.addQuickTask()
                            focusManager.clearFocus()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Catat",
                        tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Full add FAB
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(VioletGradientStart, VioletGradientEnd)
                            )
                        )
                        .clickable { showAddTaskSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "Tambah Jadwal Lengkap",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ── Bottom sheets ─────────────────────────────────────────────
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

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                ),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}



