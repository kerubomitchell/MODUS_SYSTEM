package com.example.modus_system.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.data.UserPreferences
import com.example.modus_system.ui.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Validation
    val isNameValid = name.length >= 2
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPhoneValid = phone.length >= 10
    val isPasswordValid = password.length >= 6
    val isConfirmPasswordValid = password == confirmPassword
    val isFormValid = isNameValid && isEmailValid &&
            isPhoneValid && isPasswordValid && isConfirmPasswordValid

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Text("💰", fontSize = 64.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Join Modus and take control of your finances",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Full Name
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.isNotBlank() && !isNameValid,
                supportingText = {
                    if (name.isNotBlank() && !isNameValid)
                        Text("Name must be at least 2 characters")
                }
            )
        }

        // Email
        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = email.isNotBlank() && !isEmailValid,
                supportingText = {
                    if (email.isNotBlank() && !isEmailValid)
                        Text("Enter a valid email address")
                }
            )
        }

        // Phone Number
        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phone.isNotBlank() && !isPhoneValid,
                supportingText = {
                    if (phone.isNotBlank() && !isPhoneValid)
                        Text("Enter a valid phone number")
                }
            )
        }

        // Password
        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Hide" else "Show", fontSize = 12.sp)
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = password.isNotBlank() && !isPasswordValid,
                supportingText = {
                    if (password.isNotBlank() && !isPasswordValid)
                        Text("Password must be at least 6 characters")
                }
            )
        }

        // Confirm Password
        item {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    TextButton(onClick = {
                        confirmPasswordVisible = !confirmPasswordVisible
                    }) {
                        Text(if (confirmPasswordVisible) "Hide" else "Show", fontSize = 12.sp)
                    }
                },
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPassword.isNotBlank() && !isConfirmPasswordValid,
                supportingText = {
                    if (confirmPassword.isNotBlank() && !isConfirmPasswordValid)
                        Text("Passwords do not match")
                }
            )
        }

        // Error Message
        if (errorMessage.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Register Button
        item {
            Button(
                onClick = {
                    if (isFormValid) {
                        isLoading = true
                        errorMessage = ""
                        scope.launch {
                            userPreferences.saveUser(
                                name = name,
                                email = email,
                                phone = phone,
                                password = password
                            )
                            isLoading = false
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    } else {
                        errorMessage = "Please fill in all fields correctly"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "Register Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Login link
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                TextButton(onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }) {
                    Text("Login", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}