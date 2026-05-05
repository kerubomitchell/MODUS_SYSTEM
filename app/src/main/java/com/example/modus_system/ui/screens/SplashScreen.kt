package com.example.modus_system.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.data.UserPreferences
import com.example.modus_system.ui.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = false)
    val isRegistered by userPreferences.isRegistered.collectAsState(initial = false)

    // Animations
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val shieldAlpha = remember { Animatable(0f) }
    val shieldOffset = remember { Animatable(30f) }
    val goldenAlpha = remember { Animatable(0f) }
    val goldenOffset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        // Logo bounce in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        logoAlpha.animateTo(1f, animationSpec = tween(400))

        delay(200)

        // Title fade in
        titleAlpha.animateTo(1f, animationSpec = tween(600))
        delay(200)

        // Subtitle fade in
        subtitleAlpha.animateTo(1f, animationSpec = tween(600))
        delay(200)

        // Iron Shield slide up
        shieldAlpha.animateTo(1f, animationSpec = tween(500))
        shieldOffset.animateTo(0f, animationSpec = tween(500))
        delay(150)

        // Golden Path slide up
        goldenAlpha.animateTo(1f, animationSpec = tween(500))
        goldenOffset.animateTo(0f, animationSpec = tween(500))
        delay(200)

        // Tagline
        taglineAlpha.animateTo(1f, animationSpec = tween(600))

        // Wait then navigate
        delay(1200)

        val destination = when {
            isLoggedIn -> Screen.Home.route
            isRegistered -> Screen.Login.route
            else -> Screen.Register.route
        }

        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    // Gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A1A),
                        Color(0xFF1A1A3E),
                        Color(0xFF0D0D2B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {

            // Logo
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            ) {
                Text("💎", fontSize = 96.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                "MODUS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 12.sp,
                modifier = Modifier.alpha(titleAlpha.value),
                textAlign = TextAlign.Center
            )

            // Subtitle
            Text(
                "Financial Control System",
                fontSize = 14.sp,
                color = Color(0xFFB0B0D0),
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(subtitleAlpha.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .alpha(shieldAlpha.value)
                    .width(120.dp)
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF6060FF),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Iron Shield Path
            Row(
                modifier = Modifier
                    .alpha(shieldAlpha.value)
                    .offset(y = shieldOffset.value.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1E1E4E),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛡️", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Iron Shield Path",
                        color = Color(0xFF8080FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Protect your essentials",
                        color = Color(0xFF8080AA),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Golden Path
            Row(
                modifier = Modifier
                    .alpha(goldenAlpha.value)
                    .offset(y = goldenOffset.value.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF2A2000),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✨", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Golden Path",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Grow your wealth",
                        color = Color(0xFFAA9000),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Tagline
            Text(
                "Take control of your financial destiny",
                fontSize = 13.sp,
                color = Color(0xFF6060AA),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value),
                letterSpacing = 1.sp
            )
        }
    }
}