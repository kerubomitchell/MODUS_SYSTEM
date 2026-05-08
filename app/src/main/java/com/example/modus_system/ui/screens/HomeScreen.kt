package com.example.modus_system.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.ui.Screen
import com.example.modus_system.ui.components.ModusGauge
import com.example.modus_system.ui.components.VelocityTracker
import com.example.modus_system.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val ironShieldTotal by viewModel.ironShieldTotal.collectAsState(initial = 0.0)
    val goldenPathTotal by viewModel.goldenPathTotal.collectAsState(initial = 0.0)
    val allTransactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val ironShieldCount by viewModel.ironShieldCount.collectAsState(initial = 0)
    val goldenPathCount by viewModel.goldenPathCount.collectAsState(initial = 0)
    val modusScore by viewModel.modusScore.collectAsState(initial = 0)
    val targetScore by viewModel.targetScore.collectAsState()
    val scoreGap by viewModel.scoreGap.collectAsState(initial = 0)
    val currency by viewModel.selectedCurrency.collectAsState()

    var showAnalysisDialog by remember { mutableStateOf(false) }

    val totalSpending = (ironShieldTotal ?: 0.0) + (goldenPathTotal ?: 0.0)
    val ironShieldPercent = if (totalSpending > 0)
        ((ironShieldTotal ?: 0.0) / totalSpending * 100).toInt() else 0
    val goldenPathPercent = if (totalSpending > 0)
        ((goldenPathTotal ?: 0.0) / totalSpending * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Modus", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Financial Control System", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AgentPulse.route) }) {
                        Icon(
                            Icons.Default.AutoAwesome, 
                            contentDescription = "Agent Pulse",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Modus Score & Overview Card
            item {
                val isGoldenMode = modusScore >= 50
                val scoreColor = when {
                    modusScore < 25 -> MaterialTheme.colorScheme.error
                    modusScore < 50 -> Color(0xFFFBC02D) // Amber
                    else -> Color(0xFFFFD700) // Gold
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGoldenMode) scoreColor.copy(alpha = 0.1f) 
                                         else MaterialTheme.colorScheme.primaryContainer
                    ),
                    border = if (isGoldenMode) BorderStroke(1.dp, scoreColor) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Total Spending",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isGoldenMode) scoreColor else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    "$currency ${"%.2f".format(totalSpending)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGoldenMode) scoreColor else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            // Modus Score Gauge
                            ModusGauge(
                                score = modusScore,
                                targetScore = targetScore,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp)
                                    .let { 
                                        if (totalSpending > 0) it.clickable { showAnalysisDialog = true } 
                                        else it 
                                    },
                                scoreColor = scoreColor
                            )
                        }

                        // Link to Analysis
                        if (totalSpending > 0) {
                            TextButton(
                                onClick = { navController.navigate(Screen.WealthAnalysis.route) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("View Detailed Analysis →", fontSize = 12.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Behavioral Nudge
                        VelocityTracker(
                            currentScore = modusScore,
                            targetScore = targetScore
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        val (nudgeIcon, nudgeText) = when {
                            totalSpending == 0.0 -> "🤖" to "Agent Ready. Start your journey by adding a transaction."
                            scoreGap > 0 -> "🚀" to "Velocity Alert: You are $scoreGap% away from your North Star goal ($targetScore%)."
                            scoreGap <= 0 && modusScore > 0 -> "🎯" to "North Star Achieved! You are exceeding your $targetScore% target. Keep pushing!"
                            modusScore < 25 -> "⚠️" to "Agent Warning: Your 'Iron Shield' is over-encumbered. Your growth is stalled."
                            modusScore < 50 -> "⚖️" to "Agent Analysis: Balanced behavior detected. Room for more 'Golden Path' activity."
                            else -> "🌟" to "Agent Intel: Elite behavior! Your 'Golden Path' is dominating your ecosystem."
                        }
                        
                        Surface(
                            color = scoreColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(nudgeIcon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = nudgeText,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isGoldenMode) scoreColor else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        // Split bar
                        if (totalSpending > 0) {
                            LinearProgressIndicator(
                                progress = { (ironShieldTotal ?: 0.0).toFloat() / totalSpending.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🛡️ $ironShieldPercent% Essential",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text("✨ $goldenPathPercent% Growth",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            // Two path cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Iron Shield Card
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.IronShield.route) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🛡️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Iron Shield", fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall)
                            Text("Essential", fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "$currency ${"%.2f".format(ironShieldTotal ?: 0.0)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                            Text("$ironShieldCount transactions", fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }

                    // Golden Path Card
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.GoldenPath.route) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("✨", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Golden Path", fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall)
                            Text("Growth", fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "$currency ${"%.2f".format(goldenPathTotal ?: 0.0)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 14.sp
                            )
                            Text("$goldenPathCount transactions", fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // Recent transactions header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${allTransactions.size} total",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            if (allTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💰", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions yet", fontWeight = FontWeight.Medium)
                            Text("Tap a path to add your first transaction",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                }
            } else {
                items(allTransactions.take(10)) { transaction ->
                    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (transaction.category == "IRON_SHIELD") "🛡️" else "✨",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(transaction.merchantName, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        dateFormat.format(Date(transaction.timestamp)),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    if (transaction.note.isNotBlank()) {
                                        Text(transaction.note, fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    }
                                }
                            }
                            Text(
                                "${transaction.currency} ${"%.2f".format(transaction.amount)}",
                                fontWeight = FontWeight.Bold,
                                color = if (transaction.category == "IRON_SHIELD")
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showAnalysisDialog) {
        AlertDialog(
            onDismissRequest = { showAnalysisDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖 Agent Modus Report", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Your Modus Score is $modusScore/100.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        "This score represents the ratio of your Growth (Golden Path) to your Total Spending. A higher score means you are allocating more towards your future self.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    
                    Text(
                        "Behavioral Breakdown:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val advice = when {
                        scoreGap > 20 -> "You are significantly below your North Star. The Agent recommends auditing your 'Iron Shield' for leaky expenses to reallocate to growth."
                        scoreGap > 0 -> "You are closing in on your North Star! Consistent small wins in the Golden Path will get you there."
                        scoreGap <= 0 && targetScore > 0 -> "You have surpassed your North Star. You are now in a high-velocity growth phase."
                        modusScore < 10 -> "You are in 'Survival Mode'. Your Iron Shield is under heavy pressure. Look for small ways to reduce recurring essential costs."
                        modusScore < 30 -> "You are in 'Stabilization Mode'. You have a shield, but your path forward is narrow. Try to automate a small 'Golden Path' contribution."
                        modusScore < 50 -> "You are in 'Transition Mode'. You are balancing defense and offense. One more growth investment could tip you into the Golden State."
                        else -> "You are in 'Ascension Mode'. Your behavior is growth-oriented. Continue to maintain the Iron Shield only as needed to protect this momentum."
                    }
                    
                    Text(advice, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAnalysisDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }
}
