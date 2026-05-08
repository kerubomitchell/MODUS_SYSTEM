package com.example.modus_system.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.data.Transaction
import com.example.modus_system.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthAnalysisScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val ironShieldTotal by viewModel.ironShieldTotal.collectAsState(initial = 0.0)
    val goldenPathTotal by viewModel.goldenPathTotal.collectAsState(initial = 0.0)
    val modusScore by viewModel.modusScore.collectAsState(initial = 0)
    val currency by viewModel.selectedCurrency.collectAsState()
    val transactionsByDay by viewModel.transactionsByDay.collectAsState(initial = emptyMap())

    val total = (ironShieldTotal ?: 0.0) + (goldenPathTotal ?: 0.0)
    val ironPercent = if (total > 0) ((ironShieldTotal ?: 0.0) / total).toFloat() else 0f
    val goldenPercent = if (total > 0) ((goldenPathTotal ?: 0.0) / total).toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wealth Visualizations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    "Behavioral Composition",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "How your capital is distributed across the two core paths.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            item {
                WealthDonutChart(
                    ironPercent = ironPercent,
                    goldenPercent = goldenPercent,
                    ironColor = MaterialTheme.colorScheme.primary,
                    goldenColor = MaterialTheme.colorScheme.secondary,
                    centerText = "$modusScore",
                    subText = "MODUS"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Financial Distribution", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DistributionRow(
                            label = "Iron Shield (Essentials)",
                            amount = "$currency ${"%.2f".format(ironShieldTotal ?: 0.0)}",
                            percent = "${(ironPercent * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DistributionRow(
                            label = "Golden Path (Growth)",
                            amount = "$currency ${"%.2f".format(goldenPathTotal ?: 0.0)}",
                            percent = "${(goldenPercent * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            item {
                Column {
                    Text(
                        "Investment Trend (Last 7 Days)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    WealthTrendChart(
                        transactionsByDay = transactionsByDay,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                Column {
                    Text(
                        "Agent Insight",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = getAgentInsight(modusScore),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WealthDonutChart(
    ironPercent: Float,
    goldenPercent: Float,
    ironColor: Color,
    goldenColor: Color,
    centerText: String,
    subText: String
) {
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(ironPercent, goldenPercent) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val strokeWidth = 30.dp.toPx()
            val size = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Draw Background Track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            if (ironPercent + goldenPercent > 0) {
                // Draw Iron Shield (Start from -90 degrees - Top)
                drawArc(
                    color = ironColor,
                    startAngle = -90f,
                    sweepAngle = 360f * ironPercent * animationProgress.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = size,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw Golden Path
                drawArc(
                    color = goldenColor,
                    startAngle = -90f + (360f * ironPercent),
                    sweepAngle = 360f * goldenPercent * animationProgress.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = size,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerText,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (centerText.toIntOrNull() ?: 0 >= 50) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun DistributionRow(
    label: String,
    amount: String,
    percent: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            color = color,
            shape = MaterialTheme.shapes.extraSmall
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Column(horizontalAlignment = Alignment.End) {
            Text(amount, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(percent, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
fun WealthTrendChart(
    transactionsByDay: Map<Long, List<Transaction>>,
    primaryColor: Color,
    secondaryColor: Color
) {
    val last7Days = remember(transactionsByDay) {
        val days = mutableListOf<Long>()
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        
        for (i in 0..6) {
            days.add(calendar.timeInMillis)
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        days.reversed()
    }

    val maxAmount = remember(transactionsByDay, last7Days) {
        val max = last7Days.maxOfOrNull { day ->
            val dailyTransactions = transactionsByDay[day] ?: emptyList()
            dailyTransactions.sumOf { it.amount }
        } ?: 100.0
        if (max == 0.0) 100.0 else max
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            last7Days.forEach { day ->
                val dailyTransactions = transactionsByDay[day] ?: emptyList()
                val ironAmount = dailyTransactions.filter { it.category == "IRON_SHIELD" }.sumOf { it.amount }
                val goldenAmount = dailyTransactions.filter { it.category == "GOLDEN_PATH" }.sumOf { it.amount }
                
                val ironHeight = (ironAmount / maxAmount).toFloat()
                val goldenHeight = (goldenAmount / maxAmount).toFloat()

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val totalHeight = (ironHeight + goldenHeight).coerceAtMost(1f)
                        Column(
                            modifier = Modifier.fillMaxHeight(totalHeight),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            if (goldenHeight > 0) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(goldenHeight),
                                    color = secondaryColor,
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {}
                            }
                            if (ironHeight > 0) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(ironHeight),
                                    color = primaryColor,
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {}
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(day)),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

private fun getAgentInsight(score: Int): String {
    return when {
        score == 0 -> "Agent analysis pending. Provide financial data to initialize behavioral mapping."
        score < 25 -> "Critical imbalance detected. Your resources are currently consumed by survival requirements (Iron Shield). To unlock the Golden Path, look for 'phantom expenses' that can be liquidated."
        score < 50 -> "Atmospheric stability reached. You are maintaining your shield while feeding the Golden Path. Aim for a 50/50 split to enter the 'Golden State' and maximize behavioral rewards."
        else -> "Golden State active. Your capital flow is optimized for future growth. The Iron Shield is now secondary to your expansion. Maintain this trajectory to reach peak Modus efficiency."
    }
}
