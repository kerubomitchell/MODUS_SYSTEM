package com.example.modus_system.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.modus_system.data.CurrencyPreferences
import com.example.modus_system.data.Transaction
import com.example.modus_system.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    navController: NavController,
    viewModel: TransactionViewModel,
    transactionId: Int
) {
    val currency by viewModel.selectedCurrency.collectAsState()

    var transaction by remember { mutableStateOf<Transaction?>(null) }
    var merchantName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("IRON_SHIELD") }
    var selectedCurrency by remember { mutableStateOf(currency) }
    var currencyExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    // Load transaction data
    LaunchedEffect(transactionId) {
        val loaded = viewModel.getTransactionById(transactionId)
        loaded?.let {
            transaction = it
            merchantName = it.merchantName
            amount = it.amount.toString()
            note = it.note
            selectedCategory = it.category
            selectedCurrency = it.currency
            isLoaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Edit Transaction", fontWeight = FontWeight.Bold)
                        Text(
                            if (selectedCategory == "IRON_SHIELD") "🛡️ Iron Shield"
                            else "✨ Golden Path",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (!isLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Merchant Name
                item {
                    OutlinedTextField(
                        value = merchantName,
                        onValueChange = { merchantName = it },
                        label = { Text("Merchant / Description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Amount
                item {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Note
                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Currency Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = currencyExpanded,
                        onExpandedChange = { currencyExpanded = !currencyExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCurrency,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Currency") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = currencyExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = currencyExpanded,
                            onDismissRequest = { currencyExpanded = false }
                        ) {
                            CurrencyPreferences.CURRENCIES.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text(curr) },
                                    onClick = {
                                        selectedCurrency = curr
                                        currencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Category Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (selectedCategory == "IRON_SHIELD")
                                "🛡️ Iron Shield" else "✨ Golden Path",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = categoryExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🛡️ Iron Shield") },
                                onClick = {
                                    selectedCategory = "IRON_SHIELD"
                                    categoryExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("✨ Golden Path") },
                                onClick = {
                                    selectedCategory = "GOLDEN_PATH"
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Save Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            transaction?.let { existing ->
                                if (merchantName.isNotBlank() && amount.isNotBlank()) {
                                    viewModel.updateTransaction(
                                        existing.copy(
                                            merchantName = merchantName,
                                            amount = amount.toDoubleOrNull() ?: existing.amount,
                                            note = note,
                                            category = selectedCategory,
                                            currency = selectedCurrency,
                                            isEssential = selectedCategory == "IRON_SHIELD"
                                        )
                                    )
                                    navController.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }
                }

                // Delete Button
                item {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete Transaction")
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete '$merchantName'?") },
            confirmButton = {
                TextButton(onClick = {
                    transaction?.let { viewModel.deleteTransaction(it) }
                    showDeleteDialog = false
                    navController.popBackStack()
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