package com.example.dailytask.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.HeaderDarkEnd
import com.example.dailytask.ui.theme.HeaderDarkStart
import com.example.dailytask.util.DateUtils

@Composable
fun ExpandableCalendarHeader(
    userName: String,
    greeting: String,
    motivationalQuote: String,
    totalCount: Int,
    isAllDone: Boolean,
    selectedDate: String,
    datesWithTasks: Set<String>,
    year: Int,
    month: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectDate: (String) -> Unit,
    onChangeMonth: (Int) -> Unit,
    onResetToday: () -> Unit,
    onOpenSettings: () -> Unit,
    headerColorHex: String = "#141417",
    modifier: Modifier = Modifier
) {
    val daysGrid = remember(year, month) { DateUtils.getDaysInMonthGrid(year, month) }
    val monthYearTitle = remember(year, month) { DateUtils.formatMonthYear(year, month) }
    val weekDays = remember { listOf("SEN", "SEL", "RAB", "KAM", "JUM", "SAB", "MIN") }
    val formattedDateHeader = remember(selectedDate) { DateUtils.formatDateToDisplay(selectedDate) }

    val parsedHeaderBase = remember(headerColorHex) {
        try {
            Color(android.graphics.Color.parseColor(headerColorHex))
        } catch (e: Exception) {
            HeaderDarkStart
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        parsedHeaderBase,
                        parsedHeaderBase.copy(alpha = 0.92f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 22.dp, bottom = 8.dp)
        ) {
            // ── Top Row: Editorial Greeting + Status & Settings ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, $userName",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = formattedDateHeader,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.5.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = Color.White.copy(alpha = 0.55f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f))
                        .clickable { onOpenSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Pengaturan",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Human Narrative Briefing Line ──
            AnimatedContent(
                targetState = motivationalQuote,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(140))
                },
                label = "narrativeAnim",
                modifier = Modifier.padding(horizontal = 22.dp)
            ) { quote ->
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 0.8.dp,
                        color = Color.White.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isAllDone) AccentMint else Color(0xFF60A5FA))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = quote,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                letterSpacing = 0.1.sp
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── 1-Week Horizontal Strip View (When Collapsed) ──
            AnimatedVisibility(
                visible = !isExpanded,
                enter = fadeIn(tween(180, easing = FastOutSlowInEasing)) + expandVertically(tween(220, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(120, easing = FastOutSlowInEasing)) + shrinkVertically(tween(180, easing = FastOutSlowInEasing))
            ) {
                HorizontalWeekStrip(
                    selectedDate = selectedDate,
                    datesWithTasks = datesWithTasks,
                    onSelectDate = onSelectDate,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Full Month Calendar Grid View (When Expanded) ──
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(200, easing = FastOutSlowInEasing)) + expandVertically(tween(240, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(120, easing = FastOutSlowInEasing)) + shrinkVertically(tween(180, easing = FastOutSlowInEasing))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 0.8.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Month Navigation Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { onChangeMonth(-1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronLeft,
                                        contentDescription = "Bulan Lalu",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    text = monthYearTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(HeaderDarkStart, HeaderDarkEnd)
                                                )
                                            )
                                            .clickable { onResetToday() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Today,
                                            contentDescription = "Hari Ini",
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { onChangeMonth(1) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ChevronRight,
                                            contentDescription = "Bulan Depan",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Weekday Names
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                weekDays.forEach { dayName ->
                                    Text(
                                        text = dayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.8.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Month Grid Days (Pre-chunked and optimized)
                            val rows = remember(daysGrid) { daysGrid.chunked(7) }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
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
                                                            listOf(HeaderDarkStart, HeaderDarkEnd)
                                                        ) else Brush.verticalGradient(
                                                            listOf(Color.Transparent, Color.Transparent)
                                                        )
                                                    )
                                                    .then(
                                                        if (day.isToday && !isSelected) Modifier.border(
                                                            width = 1.2.dp,
                                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                                            shape = RoundedCornerShape(12.dp)
                                                        ) else Modifier
                                                    )
                                                    .clickable {
                                                        onSelectDate(day.dateString)
                                                        onToggleExpand()
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = "${day.dayNumber}",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                                                            fontSize = 12.5.sp
                                                        ),
                                                        color = when {
                                                            isSelected -> Color.White
                                                            !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                                            day.isToday -> MaterialTheme.colorScheme.primary
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                    if (hasTasks) {
                                                        Spacer(modifier = Modifier.height(1.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .size(3.5.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) Color.White.copy(alpha = 0.85f)
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
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Minimalist Drag Handle Bar ──
            val interactionSource = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(isExpanded) {
                        var dragDistanceY = 0f
                        detectVerticalDragGestures(
                            onDragStart = { dragDistanceY = 0f },
                            onDragEnd = {
                                if (!isExpanded && dragDistanceY > 15f) {
                                    onToggleExpand()
                                } else if (isExpanded && dragDistanceY < -15f) {
                                    onToggleExpand()
                                }
                                dragDistanceY = 0f
                            },
                            onDragCancel = { dragDistanceY = 0f },
                            onVerticalDrag = { change, dragAmount ->
                                dragDistanceY += dragAmount
                                if (!isExpanded && dragDistanceY > 15f) {
                                    change.consume()
                                    onToggleExpand()
                                    dragDistanceY = 0f
                                } else if (isExpanded && dragDistanceY < -15f) {
                                    change.consume()
                                    onToggleExpand()
                                    dragDistanceY = 0f
                                }
                            }
                        )
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onToggleExpand() }
                    .padding(top = 2.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(4.5.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
        }
    }
}
