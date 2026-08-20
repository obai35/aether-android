package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun SettingsScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var apiUrl by remember { mutableStateOf("https://your-aether-backend.com") }
    var apiKey by remember { mutableStateOf("") }
    var enableNotifications by remember { mutableStateOf(true) }
    var enableBiometric by remember { mutableStateOf(true) }
    var autoSync by remember { mutableStateOf(true) }
    var syncInterval by remember { mutableStateOf(30) } // minutes

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Backend Configuration
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Backend Configuration", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                        TextField(
                            value = apiUrl,
                            onValueChange = { apiUrl = it },
                            label = { Text("Backend URL") },
                            placeholder = { Text("https://your-aether-backend.com") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                        TextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            label = { Text("API Key") },
                            placeholder = { Text("Enter your API key") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )

                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                        Button(onClick = { viewModel.updateSettings(apiUrl, apiKey) }) {
                            Text("Save Backend Settings")
                        }
                    }
                }

                // Notifications
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Push Notifications")
                            Switch(
                                checked = enableNotifications,
                                onCheckedChange = { enableNotifications = it },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }

                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Biometric Authentication")
                            Switch(
                                checked = enableBiometric,
                                onCheckedChange = { enableBiometric = it },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }

                // Sync Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Auto Sync", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Enable Auto Sync")
                            Switch(
                                checked = autoSync,
                                onCheckedChange = { autoSync = it },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }

                        if (autoSync) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                            Text("Sync Interval: $syncInterval minutes", fontWeight = FontWeight.Medium)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                            androidx.compose.material3.Slider(
                                value = syncInterval.toFloat(),
                                onValueChange = { syncInterval = it.toInt() },
                                valueRange = 5f..120f,
                                steps = 23,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // About
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                        Text("Aether Freelancer Companion", fontWeight = FontWeight.Medium)
                        Text("Version 1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text("Companion app for Aether AI Agent System freelancer automation", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}