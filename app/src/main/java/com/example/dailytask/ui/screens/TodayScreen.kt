package com.example.dailytask.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.dailytask.data.model.CategoryConstants
import com.example.dailytask.ui.components.DailyInsightCard
import com.example.dailytask.ui.components.ExpandableCalendarHeader
import com.example.dailytask.ui.components.LottieEmptyState
import com.example.dailytask.ui.components.SettingsBottomSheet
import com.example.dailytask.ui.components.TaskItemCard
import com.example.dailytask.ui.components.parseHexColor
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.HeaderDarkEnd
import com.example.dailytask.ui.theme.HeaderDarkStart
import com.example.dailytask.ui.theme.isDark
import com.example.dailytask.ui.viewmodel.DailyTaskViewModel
import com.example.dailytask.util.DateUtils
import java.util.Calendar

private fun getHumanNarrative(dateString: String, totalCount: Int): String {
    val dayLabel = DateUtils.getRelativeDayLabel(dateString)
    val lowerLabel = if (dayLabel.startsWith("Hari") || dayLabel.startsWith("Kemarin") || dayLabel.startsWith("Besok")) {
        dayLabel.lowercase(java.util.Locale.getDefault())
    } else {
        "pada $dayLabel"
    }
    return when {
        totalCount == 0 -> "Belum ada aktivitas tercatat $lowerLabel."
        totalCount == 1 -> "1 aktivitas tercatat $lowerLabel."
        else -> "Ada $totalCount aktivitas tercatat $lowerLabel."
    }
}

private fun getTimeGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..10 -> "Selamat Pagi"
        in 11..14 -> "Selamat Siang"
        in 15..18 -> "Selamat Sore"
        else -> "Selamat Malam"
    }
}

@Composable
fun TodayScreen(
    viewModel: DailyTaskViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val allTasksByDate by viewModel.allTasksByDate.collectAsState()
    val datesWithTasks by viewModel.datesWithTasks.collectAsState()
    val inputText by viewModel.todayInputText.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val year by viewModel.calendarYear.collectAsState()
    val month by viewModel.calendarMonth.collectAsState()
    val headerColorHex by viewModel.headerColorHex.collectAsState()
    val bodyColorHex by viewModel.bodyColorHex.collectAsState()

    var isCalendarExpanded by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var selectedQuickCategory by remember { mutableStateOf("Umum") }

    val focusManager = LocalFocusManager.current

    // Current selected date tasks for header counts
    val currentSelectedTasks = allTasksByDate[selectedDate] ?: emptyList()
    val completedCount = currentSelectedTasks.count { it.isCompleted }
    val totalCount = currentSelectedTasks.size

    val dynamicBodyColor = remember(bodyColorHex) {
        parseHexColor(bodyColorHex, Color(0xFFF9F9FB))
    }
    val dynamicHeaderColor = remember(headerColorHex) {
        parseHexColor(headerColorHex, Color(0xFF141417))
    }

    val isBodyDark = remember(dynamicBodyColor) { dynamicBodyColor.isDark() }
    val isHeaderDark = remember(dynamicHeaderColor) { dynamicHeaderColor.isDark() }

    // Dynamic System Navigation & Status Bar Color Sync
    val context = LocalContext.current
    val window = (context as? android.app.Activity)?.window
    if (window != null) {
        SideEffect {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = !isHeaderDark
            insetsController.isAppearanceLightNavigationBars = !isBodyDark
        }
    }

    val greeting = getTimeGreeting()
    val narrativeQuote = getHumanNarrative(selectedDate, totalCount)

    // ── Native 120Hz Hardware-Accelerated HorizontalPager for Seamless 1:1 Swipe ──
    val initialPage = remember { DateUtils.getPageForDate(selectedDate) }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { 10000 }
    )

    // When the user drags/swipes HorizontalPager, update selectedDate in ViewModel once settled
    LaunchedEffect(pagerState.settledPage) {
        val pageDate = DateUtils.getDateForPage(pagerState.settledPage)
        if (pageDate != selectedDate) {
            viewModel.selectDate(pageDate)
        }
    }

    // When user clicks a date on the calendar header or strip, smoothly animate pager to that date
    LaunchedEffect(selectedDate) {
        val targetPage = DateUtils.getPageForDate(selectedDate)
        if (!pagerState.isScrollInProgress && pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(
                page = targetPage,
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(dynamicBodyColor)
    ) {
        // ── Top Header (Fixed at top) ──────────────────────────────
        ExpandableCalendarHeader(
            userName = userName,
            greeting = greeting,
            motivationalQuote = narrativeQuote,
            totalCount = totalCount,
            isAllDone = false,
            selectedDate = selectedDate,
            datesWithTasks = datesWithTasks,
            year = year,
            month = month,
            isExpanded = isCalendarExpanded,
            headerColorHex = headerColorHex,
            onToggleExpand = { isCalendarExpanded = !isCalendarExpanded },
            onSelectDate = { viewModel.selectDate(it) },
            onChangeMonth = { viewModel.changeCalendarMonth(it) },
            onResetToday = { viewModel.resetCalendarToToday() },
            onOpenSettings = { showSettingsSheet = true }
        )

        // ── Real-Time Interactive HorizontalPager (1:1 Finger Tracking) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                val pageDate = remember(page) { DateUtils.getDateForPage(page) }
                val pageTasks = allTasksByDate[pageDate] ?: emptyList()
                val pageTotalCount = pageTasks.size
                val relativeHeading = remember(pageDate) { DateUtils.getRelativeDayLabel(pageDate) }
                val lowerPageLabel = remember(relativeHeading) {
                    if (relativeHeading.startsWith("Hari") || relativeHeading.startsWith("Kemarin") || relativeHeading.startsWith("Besok")) {
                        relativeHeading.lowercase(java.util.Locale.getDefault())
                    } else {
                        "pada $relativeHeading"
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 160.dp)
                ) {
                    // Daily Insight Card
                    if (pageTasks.isNotEmpty()) {
                        item(key = "insight_${pageDate}") {
                            Spacer(modifier = Modifier.height(14.dp))
                            DailyInsightCard(
                                tasks = pageTasks,
                                isDarkBackground = isBodyDark,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }

                    // Section Title (Dynamic Relative Day)
                    item(key = "heading_${pageDate}") {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aktivitas $relativeHeading",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    letterSpacing = (-0.3).sp
                                ),
                                color = if (isBodyDark) Color.White else Color(0xFF141417)
                            )

                            if (pageTotalCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = if (isBodyDark) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 0.8.dp,
                                        color = if (isBodyDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
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
                                            text = "$pageTotalCount aktivitas",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = if (isBodyDark) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Timeline Items or Empty State
                    if (pageTasks.isEmpty()) {
                        item(key = "empty_${pageDate}") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LottieEmptyState()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Belum ada catatan aktivitas $lowerPageLabel",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = if (isBodyDark) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Pilih kategori dan ketik aktivitas di bawah untuk mencatat",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                                    textAlign = TextAlign.Center,
                                    color = if (isBodyDark) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        items(
                            items = pageTasks,
                            key = { it.id }
                        ) { task ->
                            val index = pageTasks.indexOf(task)
                            TaskItemCard(
                                task = task,
                                isFirst = index == 0,
                                isLast = index == pageTasks.size - 1,
                                isDarkBackground = isBodyDark,
                                onDelete = { viewModel.deleteTask(task.id) },
                                modifier = Modifier.padding(horizontal = 22.dp)
                            )
                        }
                    }
                }
            }

            // ── Floating iOS Bottom Bar with Category Selector Above Input ──
            val barBg = if (isBodyDark) Color(0xFF18181D) else Color.White.copy(alpha = 0.98f)
            val barBorder = if (isBodyDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            val inputContainerBg = if (isBodyDark) Color(0xFF24242C) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding(),
                color = barBg,
                shadowElevation = if (isBodyDark) 0.dp else 18.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 0.8.dp,
                    color = barBorder
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
                            val unselectedChipBg = if (isBodyDark) Color.White.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            val unselectedChipBorder = if (isBodyDark) Color.White.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            val unselectedChipText = if (isBodyDark) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .clickable { selectedQuickCategory = cat.name },
                                shape = RoundedCornerShape(50.dp),
                                color = if (isSelected) cat.primaryColor else unselectedChipBg,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                    width = 0.8.dp,
                                    color = unselectedChipBorder
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
                                        color = if (isSelected) Color.White else unselectedChipText
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
                                        tint = if (isBodyDark) Color.White.copy(alpha = 0.45f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Catat aktivitas [$selectedQuickCategory]...",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                        color = if (isBodyDark) Color.White.copy(alpha = 0.45f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (isBodyDark) Color.White else MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = if (isBodyDark) Color.White else MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = if (isBodyDark) Color.White.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (isBodyDark) Color.White.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedContainerColor = inputContainerBg,
                                unfocusedContainerColor = inputContainerBg
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

                        // Quick Add Action Button
                        val quickAddBg = if (inputText.isNotBlank()) {
                            Brush.linearGradient(listOf(HeaderDarkStart, HeaderDarkEnd))
                        } else if (isBodyDark) {
                            Brush.linearGradient(listOf(Color(0xFF282830), Color(0xFF282830)))
                        } else {
                            Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(quickAddBg)
                                .clickable(enabled = inputText.isNotBlank()) {
                                    viewModel.addQuickTask(selectedQuickCategory)
                                    focusManager.clearFocus()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Catat",
                                tint = if (inputText.isNotBlank()) Color.White else if (isBodyDark) Color.White.copy(alpha = 0.35f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Bottom Sheets ─────────────────────────────────────────────

        if (showSettingsSheet) {
            SettingsBottomSheet(
                currentTheme = themeMode,
                currentUserName = userName,
                notificationsEnabled = notificationsEnabled,
                headerColorHex = headerColorHex,
                bodyColorHex = bodyColorHex,
                onThemeChange = { viewModel.setThemeMode(it) },
                onUserNameChange = { viewModel.setUserName(it) },
                onNotificationsChange = { viewModel.setNotificationsEnabled(it) },
                onColorsChange = { h, b -> viewModel.setCustomColors(h, b) },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
