package com.example.dailytask.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.CoralGradientEnd
import com.example.dailytask.ui.theme.CoralGradientStart
import com.example.dailytask.util.DateUtils

@Composable
fun ExpandableCalendarHeader(
    userName: String,
    greeting: String,
    motivationalQuote: String,
    totalCount: Int,
    isAllDone: Boolean,
    animatedProgress: Float,
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
    modifier: Modifier = Modifier
) {
    val daysGrid = remember(year, month) { DateUtils.getDaysInMonthGrid(year, month) }
    val monthYearTitle = remember(year, month) { DateUtils.formatMonthYear(year, month) }
    val weekDays = remember { listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min") }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chevronRot"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CoralGradientStart,
                        CoralGradientEnd,
                        CoralGradientEnd.copy(alpha = 0.0f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 18.dp, bottom = 10.dp)
        ) {
            // ── Top Bar: Greeting + Progress + Settings ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        ),
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (totalCount > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = if (isAllDone) AccentMint else Color.White,
                                trackColor = Color.White.copy(alpha = 0.25f),
                                strokeWidth = 3.5.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onOpenSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Pengaturan",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Motivational Quote Pill ──
            AnimatedContent(
                targetState = motivationalQuote,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(150))
                },
                label = "quoteAnim",
                modifier = Modifier.padding(horizontal = 22.dp)
            ) { quote ->
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = quote,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── 1-Week Horizontal Strip View (When Collapsed) ──
            AnimatedVisibility(
                visible = !isExpanded,
                enter = fadeIn(tween(200)) + expandVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                ),
                exit = fadeOut(tween(150)) + shrinkVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(isExpanded) {
                            var dragDistanceY = 0f
                            detectVerticalDragGestures(
                                onDragStart = { dragDistanceY = 0f },
                                onDragEnd = {
                                    if (dragDistanceY > 25f) {
                                        onToggleExpand()
                                    }
                                    dragDistanceY = 0f
                                },
                                onDragCancel = { dragDistanceY = 0f },
                                onVerticalDrag = { change, dragAmount ->
                                    dragDistanceY += dragAmount
                                    if (dragDistanceY > 25f) {
                                        change.consume()
                                        onToggleExpand()
                                        dragDistanceY = 0f
                                    }
                                }
                            )
                        }
                ) {
                    HorizontalWeekStrip(
                        selectedDate = selectedDate,
                        datesWithTasks = datesWithTasks,
                        onSelectDate = onSelectDate
                    )
                }
            }

            // ── Full Month Calendar Grid View (When Expanded) ──
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(250)) + expandVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                ),
                exit = fadeOut(tween(150)) + shrinkVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .pointerInput(isExpanded) {
                            var dragDistanceY = 0f
                            detectVerticalDragGestures(
                                onDragStart = { dragDistanceY = 0f },
                                onDragEnd = {
                                    if (dragDistanceY < -25f) {
                                        onToggleExpand()
                                    }
                                    dragDistanceY = 0f
                                },
                                onDragCancel = { dragDistanceY = 0f },
                                onVerticalDrag = { change, dragAmount ->
                                    dragDistanceY += dragAmount
                                    if (dragDistanceY < -25f) {
                                        change.consume()
                                        onToggleExpand()
                                        dragDistanceY = 0f
                                    }
                                }
                            )
                        }
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.97f),
                        shadowElevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                        .background(CoralGradientStart.copy(alpha = 0.1f))
                                        .clickable { onChangeMonth(-1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronLeft,
                                        contentDescription = "Bulan Lalu",
                                        tint = CoralGradientStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    text = monthYearTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
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
                                            .background(CoralGradientStart.copy(alpha = 0.1f))
                                            .clickable { onChangeMonth(1) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ChevronRight,
                                            contentDescription = "Bulan Depan",
                                            tint = CoralGradientStart,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(CoralGradientStart, CoralGradientEnd)
                                                )
                                            )
                                            .clickable { onResetToday() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Today,
                                            contentDescription = "Hari Ini",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

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
                                            fontSize = 10.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Month Grid Days
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
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
                                                    .clip(RoundedCornerShape(10.dp))
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
                                                            shape = RoundedCornerShape(10.dp)
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
                                                            fontWeight = if (isSelected || day.isToday) FontWeight.ExtraBold else FontWeight.Normal,
                                                            fontSize = 12.sp
                                                        ),
                                                        color = when {
                                                            isSelected -> Color.White
                                                            !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)
                                                            day.isToday -> CoralGradientStart
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                    if (hasTasks) {
                                                        Spacer(modifier = Modifier.height(1.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
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

            // ── Swipe Indicator / Drag Handle Bar ──
            val interactionSource = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(isExpanded) {
                        var dragDistanceY = 0f
                        detectVerticalDragGestures(
                            onDragStart = { dragDistanceY = 0f },
                            onDragEnd = {
                                if (!isExpanded && dragDistanceY > 20f) {
                                    onToggleExpand()
                                } else if (isExpanded && dragDistanceY < -20f) {
                                    onToggleExpand()
                                }
                                dragDistanceY = 0f
                            },
                            onDragCancel = { dragDistanceY = 0f },
                            onVerticalDrag = { change, dragAmount ->
                                dragDistanceY += dragAmount
                                if (!isExpanded && dragDistanceY > 20f) {
                                    change.consume()
                                    onToggleExpand()
                                    dragDistanceY = 0f
                                } else if (isExpanded && dragDistanceY < -20f) {
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
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sleek iOS/Material Drag Handle Bar
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.5.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.45f))
                )
            }
        }
    }
}

