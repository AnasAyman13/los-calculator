package com.line.of.sight.calc.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.line.of.sight.calc.ui.theme.Indigo
import com.line.of.sight.calc.ui.theme.Teal
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onDone: () -> Unit) {
    // Phase 1: Logo enters
    val logoScale = remember { Animatable(0.5f) }
    val logoAlpha = remember { Animatable(0f) }

    // Phase 2: Radar pulses
    val pulse1Radius = remember { Animatable(0f) }
    val pulse1Alpha = remember { Animatable(0f) }
    val pulse2Radius = remember { Animatable(0f) }
    val pulse2Alpha = remember { Animatable(0f) }

    // Phase 3: Developer credit
    val devAlpha = remember { Animatable(0f) }
    val devOffsetY = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        // 1. Logo appears
        coroutineScope {
            launch { logoScale.animateTo(1f, tween(500)) }
            launch { logoAlpha.animateTo(1f, tween(500)) }
        }

        // 2. Signal waves expand twice
        repeat(2) {
            pulse1Radius.snapTo(0f)
            pulse1Alpha.snapTo(0.8f)
            pulse2Radius.snapTo(0f)
            pulse2Alpha.snapTo(0.8f)

            coroutineScope {
                // First wave
                launch {
                    pulse1Radius.animateTo(300f, tween(800))
                }
                launch {
                    pulse1Alpha.animateTo(0f, tween(800))
                }
                // Second wave delayed
                launch {
                    delay(200)
                    pulse2Radius.animateTo(300f, tween(800))
                }
                launch {
                    delay(200)
                    pulse2Alpha.animateTo(0f, tween(800))
                }
            }
            delay(100) // Small pause between double-pulses
        }

        // 3. Logo fades out
        logoAlpha.animateTo(0f, tween(400))

        // 4. Developer name appears (slide up + fade)
        coroutineScope {
            launch { devAlpha.animateTo(1f, tween(500)) }
            launch { devOffsetY.animateTo(0f, tween(500)) }
        }

        // Hold developer name for ~1 second
        delay(1000)

        // 5. Navigate to next screen
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Indigo, Teal))),
        contentAlignment = Alignment.Center
    ) {
        // Radar Pulses
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = pulse1Alpha.value),
                radius = pulse1Radius.value,
                style = Stroke(width = 4f)
            )
            drawCircle(
                color = Color.White.copy(alpha = pulse2Alpha.value),
                radius = pulse2Radius.value,
                style = Stroke(width = 4f)
            )
        }

        // App Logo & Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .alpha(logoAlpha.value)
                .scale(logoScale.value)
        ) {
            Icon(
                imageVector = Icons.Default.CellTower,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "LOS Calculator",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Line-of-Sight & Radio Horizon Calculator",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        // Developer Credit (Centered after logo fades)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(devAlpha.value)
                .offset(y = devOffsetY.value.dp)
        ) {
            Text(
                text = "Developed by",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Anas Ayman El-Gebaili",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
