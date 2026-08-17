package com.example.dailytask.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.CoralGradientEnd
import com.example.dailytask.ui.theme.CoralGradientStart
import com.example.dailytask.util.DateUtils

@Composable
fun HorizontalWeekStrip(
    selectedDate: String,
    datesWithTasks: Set<String>,
    onSelectDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var weekDays by remember {
        mutableStateOf(DateUtils.getWeekDaysAround(selectedDate))
    }

    val listState = rememberLazyListState()

    // Scroll to today / selected date
    LaunchedEffect(selectedDate) {
        var index = weekDays.indexOfFirst { it.dateString == selectedDate }
        if (index == -1) {
            weekDays = DateUtils.getWeekDaysAround(selectedDate)
            index = weekDays.indexOfFirst { it.dateString == selectedDate }
            listState.scrollToItem((index - 2).coerceAtLeast(0))
        } else {
            listState.animateScrollToItem((index - 2).coerceAtLeast(0))
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        items(weekDays, key = { it.dateString }) { day ->
            val isSelected = day.dateString == selectedDate
            val hasTasks = datesWithTasks.contains(day.dateString)

            val itemScale by animateFloatAsState(
                targetValue = if (isSelected) 1.06f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dayItemScale"
            )

            val dayNumColor by animateColorAsState(
                targetValue = when {
                    isSelected -> Color.White
                    day.isToday -> CoralGradientStart
                    else -> MaterialTheme.colorScheme.onSurface
                },
                label = "dayNumColor"
            )

            Box(
                modifier = Modifier
                    .scale(itemScale)
                    .width(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (isSelected) {
                            Brush.verticalGradient(
                                colors = listOf(CoralGradientStart, CoralGradientEnd)
                            )
                        } else if (day.isToday) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    CoralGradientStart.copy(alpha = 0.1f),
                                    CoralGradientEnd.copy(alpha = 0.06f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            )
                        }
                    )
                    .clickable { onSelectDate(day.dateString) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day.dayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = when {
                            isSelected -> Color.White.copy(alpha = 0.75f)
                            day.isToday -> CoralGradientStart.copy(alpha = 0.8f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = day.dayNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected || day.isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = dayNumColor
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // Task indicator dot
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    hasTasks && isSelected -> Color.White.copy(alpha = 0.85f)
                                    hasTasks -> AccentMint
                                    else -> Color.Transparent
                                }
                            )
                    )
                }
            }
        }
    }
}
