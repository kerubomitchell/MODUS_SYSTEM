package com.example.modus_system.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.data.CurrencyPreferences
import com.example.modus_system.data.UserPreferences
import com.example.modus_system.ui.Screen
import com.example.modus_system.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }

    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val userName by userPreferences.userName.collectAsState(initial = "")
    val userEmail by userPreferences.userEmail.collectAsState(initial = "")
    val userPhone by userPreferences.userPhone.collectAsState(initial = "")

    var expanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Section
            item {
                Text("Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Avatar
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = MaterialTheme.shapes.extraLarge,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Text(
                                    text = if (userName.isNotBlank())
                                        userName.first().uppercase() else "?",
                                    modifier = Modifier.padding(24.dp),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Full Name",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    userName.ifBlank { "Not set" },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Email
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Email",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    userEmail.ifBlank { "Not set" },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Phone
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Phone",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    userPhone.ifBlank { "Not set" },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Currency Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Currency", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Choose your preferred currency",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCurrency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Selected Currency") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                CurrencyPreferences.CURRENCIES.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(currency) },
                                        onClick = {
                                            viewModel.updateCurrency(currency)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // About Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("About", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Modus App", fontWeight = FontWeight.Bold)
                        Text(
                            "Version 1.0",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "🛡️ Iron Shield — Tracks essential survival expenses.\n\n" +
                                    "✨ Golden Path — Tracks growth investments to build wealth.",
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        userPreferences.logout()
                        showLogoutDialog = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}