package com.example.modus_system.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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
fun GoldenPathScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val transactions by viewModel.goldenPathTransactions.collectAsState(initial = emptyList())
    val total by viewModel.goldenPathTotal.collectAsState(initial = 0.0)
    val currency by viewModel.selectedCurrency.collectAsState()
    val searchQuery by viewModel.goldenPathSearchQuery.collectAsState()

    var merchantName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember {
        mutableStateOf<com.example.modus_system.data.Transaction?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateGoldenPathSearch(it) },
                            placeholder = { Text("Search investments...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
                                    .copy(alpha = 0.5f)
                            )
                        )
                    } else {
                        Column {
                            Text("✨ Golden Path", fontWeight = FontWeight.Bold)
                            Text(
                                "Growth & Investment",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showSearch) {
                            showSearch = false
                            viewModel.updateGoldenPathSearch("")
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSearch = !showSearch
                        if (!showSearch) viewModel.updateGoldenPathSearch("")
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = !showForm },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    if (showForm) "✕" else "+",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total Growth Investments",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$currency ${"%.2f".format(total ?: 0.0)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${transactions.size} transactions",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Add Form
            if (showForm) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Add Growth Investment",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = merchantName,
                                onValueChange = { merchantName = it },
                                label = { Text("Investment / Description") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { amount = it },
                                label = { Text("Amount ($currency)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = note,
                                onValueChange = { note = it },
                                label = { Text("Note (optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (merchantName.isNotBlank() && amount.isNotBlank()) {
                                        viewModel.addTransaction(
                                            merchantName = merchantName,
                                            amount = amount.toDoubleOrNull() ?: 0.0,
                                            category = "GOLDEN_PATH",
                                            isEssential = false,
                                            note = note,
                                            currency = currency
                                        )
                                        merchantName = ""
                                        amount = ""
                                        note = ""
                                        showForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Save Investment")
                            }
                        }
                    }
                }
            }

            // Search results info
            if (searchQuery.isNotBlank()) {
                item {
                    Text(
                        "${transactions.size} results for \"$searchQuery\"",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                item {
                    Text("Investments", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (searchQuery.isNotBlank()) "🔍" else "✨",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (searchQuery.isNotBlank())
                                    "No results for \"$searchQuery\""
                                else "No growth investments yet"
                            )
                            if (searchQuery.isBlank()) {
                                Text(
                                    "Tap + to add one",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            } else {
                items(transactions) { transaction ->
                    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    transaction.merchantName,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    dateFormat.format(Date(transaction.timestamp)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                if (transaction.note.isNotBlank()) {
                                    Text(
                                        transaction.note,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Text(
                                "${transaction.currency} ${"%.2f".format(transaction.amount)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            // Edit Button
                            IconButton(onClick = {
                                navController.navigate(
                                    Screen.EditTransaction.createRoute(transaction.id)
                                )
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            // Delete Button
                            IconButton(onClick = {
                                transactionToDelete = transaction
                                showDeleteDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = {
                Text("Are you sure you want to delete '${transactionToDelete?.merchantName}'?")
            },
            confirmButton = {
                TextButton(onClick = {
                    transactionToDelete?.let { viewModel.deleteTransaction(it) }
                    showDeleteDialog = false
                    transactionToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}