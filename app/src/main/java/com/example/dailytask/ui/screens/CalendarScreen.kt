package com.example.dailytask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.ui.components.AddTaskBottomSheet
import com.example.dailytask.ui.components.LottieEmptyState
import com.example.dailytask.ui.components.TaskItemCard
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.CoralGradientEnd
import com.example.dailytask.ui.theme.CoralGradientStart
import com.example.dailytask.ui.theme.VioletGradientEnd
import com.example.dailytask.ui.theme.VioletGradientStart
import com.example.dailytask.ui.viewmodel.DailyTaskViewModel
import com.example.dailytask.util.DateUtils

@Composable
fun CalendarScreen(
    viewModel: DailyTaskViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.selectedDateTasks.collectAsState()
    val datesWithTasks by viewModel.datesWithTasks.collectAsState()
    val year by viewModel.calendarYear.collectAsState()
    val month by viewModel.calendarMonth.collectAsState()

    var showAddTaskSheet by remember { mutableStateOf(false) }

    val daysGrid = remember(year, month) { DateUtils.getDaysInMonthGrid(year, month) }
    val monthYearTitle = remember(year, month) { DateUtils.formatMonthYear(year, month) }
    val selectedDateTitle = remember(selectedDate) { DateUtils.formatDateToDisplay(selectedDate) }
    val weekDays = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Calendar Header ──────────────────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                    shadowElevation = 8.dp,
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        // Month navigation row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous month
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                    .clickable { viewModel.changeCalendarMonth(-1) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ChevronLeft,
                                    contentDescription = "Bulan Sebelumnya",
                                    modifier = Modifier.size(22.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Month-year title
                            Text(
                                text = monthYearTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Next month
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                        .clickable { viewModel.changeCalendarMonth(1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "Bulan Berikutnya",
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                // Back to today
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(CoralGradientStart.copy(alpha = 0.15f), CoralGradientEnd.copy(alpha = 0.1f))
                                            )
                                        )
                                        .clickable { viewModel.resetCalendarToToday() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Today,
                                        contentDescription = "Hari Ini",
                                        tint = CoralGradientStart,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weekday labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekDays.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Calendar grid
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val rows = daysGrid.chunked(7)
                            rows.forEach { week ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    week.forEach { day ->
                                        val isSelected = day.dateString == selectedDate
                                        val hasTasks = datesWithTasks.contains(day.dateString)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (isSelected) Brush.verticalGradient(
                                                        listOf(CoralGradientStart, CoralGradientEnd)
                                                    ) else Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Transparent)
                                                    )
                                                )
                                                .then(
                                                    if (day.isToday && !isSelected) Modifier.border(
                                                        width = 1.5.dp,
                                                        color = CoralGradientStart,
                                                        shape = RoundedCornerShape(12.dp)
                                                    ) else Modifier
                                                )
                                                .clickable { viewModel.selectDate(day.dateString) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "${day.dayNumber}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected || day.isToday) FontWeight.ExtraBold else FontWeight.Normal,
                                                        fontSize = 13.sp
                                                    ),
                                                    color = when {
                                                        isSelected -> Color.White
                                                        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                                        day.isToday -> CoralGradientStart
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                                if (hasTasks) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) Color.White.copy(alpha = 0.8f)
                                                                else AccentMint
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Selected date task section ───────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aktivitas & Jadwal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            ),
                            color = CoralGradientStart
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedDateTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = CoralGradientStart.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${tasks.size} tugas",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CoralGradientStart
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tasks or empty state
            if (tasks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieEmptyState()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak Ada Catatan pada Tanggal Ini",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tambahkan catatan aktivitas atau jadwal baru untuk tanggal ini.",
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

        // ── Violet FAB ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(24.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(VioletGradientStart, VioletGradientEnd))
                )
                .clickable { showAddTaskSheet = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Tambah Jadwal",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // Add Task Sheet
        if (showAddTaskSheet) {
            AddTaskBottomSheet(
                initialDate = selectedDate,
                onDismiss = { showAddTaskSheet = false },
                onSave = { task -> viewModel.addTask(task) }
            )
        }
    }
}


