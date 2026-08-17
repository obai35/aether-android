package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aether.companion.R
import com.aether.companion.data.model.AIAssistant.AIMessage
import com.aether.companion.data.model.AIAssistant.MessageRole
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun AssistantScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val messages by viewModel.assistantMessages.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(painterResource(R.drawable.ic_arrow_back), "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Welcome message if empty
            if (messages.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_smart_toy), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                        Text("Aether AI Assistant", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Ask me about your freelance jobs, automation status, or request actions!", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip("Show my jobs") { inputText = "Show my jobs" }
                            SuggestionChip("Start auto mission") { inputText = "Start an auto mission for Python web scraping" }
                            SuggestionChip("Export deliverable") { inputText = "Export the latest completed job" }
                        }
                    }
                }
            }

            // Messages List
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { message ->
                        MessageBubble(message)
                    }
                }

                if (isLoading) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.padding(start = 56.dp))
                    }
                }
            }

            // Input Area
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = { Text("Ask me anything...") },
                        singleLine = true,
                        onKeyboardAction = { action ->
                            if (action == androidx.compose.ui.text.input.ImeAction.Send) {
                                sendMessage()
                            }
                        }
                    )
                    IconButton(
                        onClick = { sendMessage() },
                        enabled = inputText.isNotBlank() && !isLoading
                    ) {
                        Icon(painterResource(R.drawable.ic_send), "Send", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    fun sendMessage() {
        if (inputText.isNotBlank() && !isLoading) {
            val text = inputText
            inputText = ""
            isLoading = true
            viewModel.sendAssistantMessage(text)
            isLoading = false
        }
    }
}

@Composable
fun MessageBubble(message: AIMessage) {
    val isUser = message.role == MessageRole.USER
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = bubbleColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = if (isUser) 0.dp else 16.dp,
            topEnd = if (isUser) 16.dp else 16.dp,
            bottomStart = 16.dp,
            bottomEnd = if (isUser) 16.dp else 0.dp
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = alignment,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (message.toolCalls != null && message.toolCalls!!.isNotEmpty()) {
                message.toolCalls!!.forEach { toolCall ->
                    androidx.compose.material3.Chip(
                        onClick = {},
                        colors = androidx.material3.ChipDefaults.chipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text("🔧 ${toolCall.function.name}", fontSize = 12.sp)
                    }
                }
            }
            Text(message.content, color = textColor, fontSize = 14.sp)
            Text(
                java.text.SimpleDateFormat("HH:mm").format(java.util.Date(message.timestamp)),
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    androidx.compose.material3.Chip(
        onClick = onClick,
        colors = androidx.material3.ChipDefaults.chipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(text, fontSize = 12.sp)
    }
}