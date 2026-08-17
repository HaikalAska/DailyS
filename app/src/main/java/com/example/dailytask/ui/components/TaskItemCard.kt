package com.example.dailytask.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytask.data.model.CategoryConstants
import com.example.dailytask.data.model.TaskEntity

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onToggle: () -> Unit = {}
) {
    val categoryInfo = remember(task.category) {
        CategoryConstants.getCategoryInfo(task.category)
    }

    var isExpanded by remember { mutableStateOf(false) }
    val spineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)

    val timeWidth = 48.dp
    val timeToDotGap = 10.dp
    val dotContainerWidth = 16.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .drawBehind {
                val strokeW = 2.dp.toPx()
                val centerX = timeWidth.toPx() + timeToDotGap.toPx() + (dotContainerWidth.toPx() / 2f)
                val dotCenterY = 10.dp.toPx()

                // Continuous spine line from top of row to dot
                if (!isFirst) {
                    drawLine(
                        color = spineColor,
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, dotCenterY),
                        strokeWidth = strokeW
                    )
                }

                // Continuous spine line from dot to bottom of row (seamless connection to next item)
                if (!isLast) {
                    drawLine(
                        color = spineColor,
                        start = Offset(centerX, dotCenterY),
                        end = Offset(centerX, size.height),
                        strokeWidth = strokeW
                    )
                }
            }
            .padding(vertical = 2.dp)
    ) {
        // ── 1. Left Time Column (e.g. 09:00, 12:30) ──
        Box(
            modifier = Modifier
                .width(timeWidth)
                .padding(top = 1.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = task.time.ifEmpty { "•" },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    letterSpacing = (-0.2).sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
            )
        }

        Spacer(modifier = Modifier.width(timeToDotGap))

        // ── 2. Center Node Dot (Positioned exactly on the spine line) ──
        Box(
            modifier = Modifier
                .width(dotContainerWidth)
                .padding(top = 3.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(categoryInfo.primaryColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(categoryInfo.primaryColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ── 3. Right Clean Borderless Activity Content ──
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Activity Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = if (isExpanded) 10 else 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.description.isNotEmpty()) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expandable Description
            AnimatedVisibility(
                visible = isExpanded && task.description.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Pill Tag
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = categoryInfo.containerColor.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = categoryInfo.icon,
                        contentDescription = null,
                        modifier = Modifier.size(9.dp),
                        tint = categoryInfo.primaryColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = categoryInfo.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = categoryInfo.primaryColor
                    )
                }
            }
        }
    }
}
