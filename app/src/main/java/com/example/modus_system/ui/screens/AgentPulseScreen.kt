package com.example.modus_system.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.modus_system.ui.components.ModusGauge
import com.example.modus_system.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentPulseScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val modusScore by viewModel.modusScore.collectAsState(initial = 0)
    val targetScore by viewModel.targetScore.collectAsState()
    val scoreGap by viewModel.scoreGap.collectAsState(initial = 0)
    val recurringCosts by viewModel.recurringShieldCosts.collectAsState(initial = emptyList())
    
    val messages = remember { mutableStateListOf<AgentMessage>() }
    var isTyping by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        messages.clear()
        delay(500)
        messages.add(AgentMessage("Hello. I am the MODUS Behavioral Agent. Analyzing your financial ecosystem...", false))
        delay(1500)
        
        // Insight 1: General Score
        val scoreMsg = when {
            modusScore >= 70 -> "Your velocity is exceptional. You are allocating $modusScore% of your capital to growth. This is the 'Ascension' state."
            modusScore >= 50 -> "You have achieved stability in the Golden State. $modusScore% growth-to-total ratio is a healthy equilibrium."
            else -> "Your current Modus Score is $modusScore%. My analysis suggests your defensive shield is consuming too much energy."
        }
        messages.add(AgentMessage(scoreMsg, false))
        delay(1200)

        // Insight 2: Target Score
        if (scoreGap > 0) {
            messages.add(AgentMessage("You are $scoreGap% away from your North Star goal of $targetScore%. To bridge this gap, consider reallocating 5% of your 'Iron Shield' costs to the 'Golden Path' this week.", false))
            delay(1500)
        } else if (targetScore > 0) {
            messages.add(AgentMessage("You have surpassed your North Star of $targetScore%. Your behavior is now self-optimizing. I am monitoring for potential new growth thresholds.", false))
            delay(1500)
        }

        // Insight 3: Leaky Shield
        if (recurringCosts.isNotEmpty()) {
            val leaks = recurringCosts.take(2).joinToString(", ") { it.first }
            messages.add(AgentMessage("Warning: I have detected recurring friction points at $leaks. These are 'Shield Leaks' that slow your momentum.", true))
            delay(1500)
        }

        messages.add(AgentMessage("I will continue to monitor your transactions. Every choice either strengthens your shield or accelerates your path. Choose growth.", false))
        isTyping = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agent Pulse", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header Stats
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ModusGauge(
                        score = modusScore,
                        targetScore = targetScore,
                        modifier = Modifier.size(80.dp)
                    )
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "STATUS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (scoreGap <= 0) "OPTIMIZED" else "REALLOCATING",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (scoreGap <= 0) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    AgentChatBubble(message)
                }
                
                if (isTyping) {
                    item {
                        Text(
                            "Agent is calculating...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            
            // Interaction area (simplified for now)
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Behavioral Analysis Active",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

data class AgentMessage(
    val text: String,
    val isAlert: Boolean = false
)

@Composable
fun AgentChatBubble(message: AgentMessage) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        color = if (message.isAlert) MaterialTheme.colorScheme.errorContainer 
                                else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isAlert) MaterialTheme.colorScheme.onErrorContainer 
                            else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
