package com.example.modus_system.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.ui.Screen
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
    val currency by viewModel.selectedCurrency.collectAsState()

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
            // Total Overview Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total Spending",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$currency ${"%.2f".format(totalSpending)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
}
