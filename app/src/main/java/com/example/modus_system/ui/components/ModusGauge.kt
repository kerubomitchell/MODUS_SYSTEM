package com.example.modus_system.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModusGauge(
    score: Int,
    targetScore: Int,
    modifier: Modifier = Modifier,
    scoreColor: Color = Color(0xFFFFD700)
) {
    val animatedScore = remember { Animatable(0f) }
    
    LaunchedEffect(score) {
        animatedScore.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val innerStrokeWidth = 4.dp.toPx()
            
            // Background Track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Target Marker (Optional visual hint)
            drawArc(
                color = scoreColor.copy(alpha = 0.3f),
                startAngle = 135f,
                sweepAngle = (targetScore.toFloat() / 100f) * 270f,
                useCenter = false,
                style = Stroke(width = innerStrokeWidth, cap = StrokeCap.Round)
            )

            // Actual Score
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        scoreColor.copy(alpha = 0.5f),
                        scoreColor
                    )
                ),
                startAngle = 135f,
                sweepAngle = (animatedScore.value / 100f) * 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (score >= targetScore) scoreColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "MODUS SCORE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Goal: $targetScore%",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = scoreColor.copy(alpha = 0.8f)
            )
        }
    }
}
