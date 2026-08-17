package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
            navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(painterResource(R.drawable.ic_arrow_back), "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Connection", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextField(
                        value = apiUrl,
                        onValueChange = { apiUrl = it },
                        label = { Text("Backend URL") },
                        placeholder = { Text("https://your-backend.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    TextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        placeholder = { Text("Enter your API key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* Test connection */ }
                    ) {
                        Text("Test Connection")
                    }
                }
            }

            // Notification Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Notifications", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Column {
                            Text("Push Notifications")
                            Text("Receive notifications for job updates", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = enableNotifications, onCheckedChange = { enableNotifications = it })
                    }
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Column {
                            Text("Auto Sync")
                            Text("Automatically sync job status", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = autoSync, onCheckedChange = { autoSync = it })
                    }
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Sync Interval: ${syncInterval} min")
                        Slider(
                            value = syncInterval.toFloat(),
                            onValueChange = { syncInterval = it.roundToInt() },
                            valueRange = 5f..120f,
                            steps = 23
                        )
                    }
                }
            }

            // Security Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Security", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Column {
                            Text("Biometric Authentication")
                            Text("Require fingerprint/face to open app", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = enableBiometric, onCheckedChange = { enableBiometric = it })
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* Change API key */ }
                    ) {
                        Text("Change API Key")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* Clear local data */ }
                    ) {
                        Text("Clear Local Data")
                    }
                }
            }

            // About
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("About", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    DetailRow("App Version", "1.0.0")
                    DetailRow("Backend API", "Aether AI Agent System")
                    DetailRow("Build Date", "2025-01-15")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}