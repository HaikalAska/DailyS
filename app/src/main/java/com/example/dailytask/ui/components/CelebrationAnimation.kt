package com.example.dailytask.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun ConfettiBurst(
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFF3B82F6),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFFEC4899),
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4)
    )

    val particles = remember {
        List(40) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 400f + 200f
            Particle(
                x = 0f,
                y = 0f,
                speedX = (Math.cos(angle.toDouble()) * speed).toFloat(),
                speedY = (Math.sin(angle.toDouble()) * speed).toFloat(),
                radius = Random.nextFloat() * 6f + 4f,
                color = colors.random()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = LinearOutSlowInEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 3f

        particles.forEach { particle ->
            val p = progress.value
            val currentX = centerX + particle.speedX * p
            val currentY = centerY + particle.speedY * p + (300f * p * p) // gravity
            val alpha = (1f - p).coerceIn(0f, 1f)

            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.radius * (1f - p * 0.5f),
                center = Offset(currentX, currentY)
            )
        }
    }
}

@Composable
fun LottieEmptyState(
    modifier: Modifier = Modifier
) {
    // Pulse animation
    val pulse = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        pulse.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = 0.08f),
                radius = (size.minDimension / 2f) * pulse.value
            )
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                radius = (size.minDimension / 3f) * pulse.value
            )
        }
    }
}
