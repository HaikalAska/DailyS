package com.example.dailytask.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.data.model.CategoryConstants
import com.example.dailytask.data.model.TaskEntity
import com.example.dailytask.ui.theme.AccentAmber
import com.example.dailytask.ui.theme.AccentAmberLight
import com.example.dailytask.ui.theme.AccentMint
import com.example.dailytask.ui.theme.AccentMintLight
import com.example.dailytask.ui.theme.AccentRed
import com.example.dailytask.ui.theme.AccentRedLight

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryInfo = remember(task.category) {
        CategoryConstants.getCategoryInfo(task.category)
    }

    var isExpanded by remember { mutableStateOf(false) }

    val checkboxScale by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "checkboxScale"
    )

    val priorityAccentColor = when (task.priority) {
        "Tinggi" -> AccentRed
        "Sedang" -> AccentAmber
        else -> AccentMint
    }
    val priorityContainerColor = when (task.priority) {
        "Tinggi" -> AccentRedLight
        "Sedang" -> AccentAmberLight
        else -> AccentMintLight
    }

    // Card surface color based on completion
    val cardColor by animateColorAsState(
        targetValue = if (task.isCompleted)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        else
            MaterialTheme.colorScheme.surface,
        label = "cardColor"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = cardColor,
        shadowElevation = if (task.isCompleted) 0.dp else 4.dp,
        tonalElevation = 0.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar — colored by category
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(if (isExpanded && task.description.isNotEmpty()) 120.dp else 80.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                    .background(
                        if (task.isCompleted) AccentMint.copy(alpha = 0.35f)
                        else categoryInfo.primaryColor
                    )
            )

            // Card content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(start = 14.dp, end = 12.dp, top = 14.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated bouncy checkbox
                    Box(
                        modifier = Modifier
                            .scale(checkboxScale)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (task.isCompleted) AccentMint else Color.Transparent
                            )
                            .then(
                                if (!task.isCompleted) Modifier.background(
                                    categoryInfo.primaryColor.copy(alpha = 0.08f),
                                    CircleShape
                                ) else Modifier
                            )
                            .clickable { onToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selesai",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(13.dp)
                                    .clip(CircleShape)
                                    .background(categoryInfo.primaryColor.copy(alpha = 0.35f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title
                    Text(
                        text = task.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            fontSize = 15.sp
                        ),
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isExpanded) 10 else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Expand / Delete actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (task.description.isNotEmpty()) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Badges row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(start = 40.dp)
                ) {
                    // Category badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryInfo.containerColor.copy(alpha = if (task.isCompleted) 0.4f else 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = categoryInfo.icon,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = categoryInfo.primaryColor.copy(alpha = if (task.isCompleted) 0.5f else 1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = categoryInfo.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = categoryInfo.primaryColor.copy(alpha = if (task.isCompleted) 0.5f else 1f)
                            )
                        }
                    }

                    // Time badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = task.displayTime,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Priority badge (only show for Tinggi/Sedang)
                    if (task.priority != "Normal") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = priorityContainerColor.copy(alpha = if (task.isCompleted) 0.3f else 0.9f)
                        ) {
                            Text(
                                text = task.priority,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = priorityAccentColor.copy(alpha = if (task.isCompleted) 0.4f else 1f)
                            )
                        }
                    }

                    // Reminder badge
                    if (task.hasReminder) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Alarm,
                                contentDescription = "Pengingat",
                                modifier = Modifier
                                    .padding(horizontal = 5.dp, vertical = 3.dp)
                                    .size(10.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // Description on expand
                AnimatedVisibility(
                    visible = isExpanded && task.description.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 40.dp, end = 4.dp)
                    ) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}


