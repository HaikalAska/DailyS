package com.example.dailytask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.util.DateUtils

@Composable
fun HorizontalWeekStrip(
    selectedDate: String,
    datesWithTasks: Set<String>,
    onSelectDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekDays = remember(selectedDate) {
        DateUtils.getWeekDaysAround(selectedDate)
    }

    val listState = rememberLazyListState()

    // Smooth lightweight scroll to selected date
    LaunchedEffect(selectedDate) {
        val index = weekDays.indexOfFirst { it.dateString == selectedDate }
        if (index >= 0 && !listState.isScrollInProgress) {
            val targetScroll = (index - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
    ) {
        items(
            items = weekDays,
            key = { it.dateString }
        ) { day ->
            val isSelected = day.dateString == selectedDate
            val hasTasks = datesWithTasks.contains(day.dateString)

            val itemBg = if (isSelected) {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF383844),
                        Color(0xFF1E1E24)
                    )
                )
            } else if (day.isToday) {
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.10f)
                    )
                )
            } else {
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.09f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            }

            val itemBorder = if (isSelected) {
                androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.45f))
            } else if (day.isToday) {
                androidx.compose.foundation.BorderStroke(1.2.dp, Color.White.copy(alpha = 0.30f))
            } else {
                androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.12f))
            }

            Box(
                modifier = Modifier
                    .width(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(itemBg)
                    .border(itemBorder.width, itemBorder.brush, RoundedCornerShape(18.dp))
                    .clickable { onSelectDate(day.dateString) }
                    .padding(vertical = 11.dp),
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
                            isSelected -> Color.White
                            day.isToday -> Color.White.copy(alpha = 0.95f)
                            else -> Color.White.copy(alpha = 0.55f)
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = day.dayNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected || day.isToday) FontWeight.Black else FontWeight.Bold,
                            fontSize = 18.5.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Minimalist task dot
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    hasTasks && isSelected -> Color.White
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
