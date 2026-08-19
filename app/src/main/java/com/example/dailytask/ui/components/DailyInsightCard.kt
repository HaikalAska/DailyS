package com.example.dailytask.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.data.model.CategoryConstants
import com.example.dailytask.data.model.TaskEntity

data class CategoryDurationStat(
    val categoryName: String,
    val totalMinutes: Int,
    val formattedDuration: String,
    val percentage: Float,
    val color: Color
)

data class DailyInsightData(
    val totalMinutes: Int,
    val formattedTotalTime: String,
    val dominantCategory: CategoryDurationStat?,
    val breakdown: List<CategoryDurationStat>
)

object InsightCalculator {

    private fun parseTimeToMinutes(timeStr: String): Int? {
        if (timeStr.isBlank()) return null
        return try {
            val parts = timeStr.trim().split(":")
            if (parts.size >= 2) {
                val hour = parts[0].toIntOrNull() ?: return null
                val min = parts[1].toIntOrNull() ?: return null
                (hour * 60) + min
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun formatMinutesToDuration(minutes: Int): String {
        if (minutes <= 0) return "0m"
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 && mins > 0 -> "${hours}j ${mins}m"
            hours > 0 -> "${hours}j"
            else -> "${mins}m"
        }
    }

    fun calculateInsight(tasks: List<TaskEntity>): DailyInsightData? {
        if (tasks.isEmpty()) return null

        val sortedTasks = tasks.sortedBy { it.timestamp }
        val categoryMinutesMap = mutableMapOf<String, Int>()

        for (i in sortedTasks.indices) {
            val currentTask = sortedTasks[i]
            val currentMins = parseTimeToMinutes(currentTask.time)
            val nextMins = if (i + 1 < sortedTasks.size) parseTimeToMinutes(sortedTasks[i + 1].time) else null

            val duration = when {
                currentMins != null && nextMins != null && nextMins >= currentMins -> {
                    val diff = nextMins - currentMins
                    if (diff == 0) 30 else diff.coerceAtMost(300)
                }
                currentMins != null && nextMins != null && nextMins < currentMins -> {
                    val diff = (1440 - currentMins) + nextMins
                    diff.coerceAtMost(300)
                }
                else -> 45
            }

            val category = currentTask.category.ifBlank { "Umum" }
            categoryMinutesMap[category] = (categoryMinutesMap[category] ?: 0) + duration
        }

        val totalMinutes = categoryMinutesMap.values.sum()
        if (totalMinutes == 0) return null

        val breakdown = categoryMinutesMap.map { (catName, minutes) ->
            val catInfo = CategoryConstants.getCategoryInfo(catName)
            val percentage = minutes.toFloat() / totalMinutes
            CategoryDurationStat(
                categoryName = catName,
                totalMinutes = minutes,
                formattedDuration = formatMinutesToDuration(minutes),
                percentage = percentage,
                color = catInfo.primaryColor
            )
        }.sortedByDescending { it.totalMinutes }

        val dominant = breakdown.firstOrNull()

        return DailyInsightData(
            totalMinutes = totalMinutes,
            formattedTotalTime = formatMinutesToDuration(totalMinutes),
            dominantCategory = dominant,
            breakdown = breakdown
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyInsightCard(
    tasks: List<TaskEntity>,
    modifier: Modifier = Modifier,
    isDarkBackground: Boolean = false
) {
    val insight = remember(tasks) {
        InsightCalculator.calculateInsight(tasks)
    }

    if (insight == null || tasks.isEmpty()) return

    val cardBg = if (isDarkBackground) Color(0xFF1C1C22) else Color.White
    val cardBorder = if (isDarkBackground) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val titleColor = if (isDarkBackground) Color.White else MaterialTheme.colorScheme.onSurface
    val subTextColor = if (isDarkBackground) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
    val pillBg = if (isDarkBackground) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val pillText = if (isDarkBackground) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        shadowElevation = if (isDarkBackground) 0.dp else 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 0.8.dp,
            color = cardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // ── 1. Header: Title + Total Time Pill ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkBackground) 0.25f else 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Insights,
                            contentDescription = null,
                            tint = if (isDarkBackground) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ringkasan Aktivitas",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = titleColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = pillBg,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 0.8.dp,
                        color = if (isDarkBackground) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = pillText
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Total ${insight.formattedTotalTime}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = pillText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── 2. Narrative Insight Text ──
            if (insight.dominantCategory != null) {
                val dominant = insight.dominantCategory
                val annotatedText = buildAnnotatedString {
                    append("Hari ini kamu paling banyak waktu di kategori ")
                    withStyle(
                        style = SpanStyle(
                            color = dominant.color,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(dominant.categoryName)
                    }
                    append(" (")
                    withStyle(
                        style = SpanStyle(
                            color = if (isDarkBackground) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(dominant.formattedDuration)
                    }
                    append(")")
                }

                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp
                    ),
                    color = subTextColor
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── 3. Multi-segment Battery Usage Style Bar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isDarkBackground) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                insight.breakdown.forEach { stat ->
                    val weight = stat.percentage.coerceAtLeast(0.02f)
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(9.dp)
                            .background(stat.color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── 4. Category Breakdown Legend ──
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                insight.breakdown.forEach { stat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.5.dp)
                                .clip(CircleShape)
                                .background(stat.color)
                        )
                        Text(
                            text = stat.categoryName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            ),
                            color = titleColor
                        )
                        Text(
                            text = "${(stat.percentage * 100).toInt()}% (${stat.formattedDuration})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (isDarkBackground) Color.White.copy(alpha = 0.65f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }
    }
}
