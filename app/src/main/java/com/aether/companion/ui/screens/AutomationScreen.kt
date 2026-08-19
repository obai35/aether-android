package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aether.companion.R
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun AutomationScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedPlatforms by remember { mutableStateOf(listOf("remoteok", "mostaql")) }
    var minBudget by remember { mutableStateOf(100) }
    var maxBudget by remember { mutableStateOf(5000) }
    var qualityThreshold by remember { mutableStateOf(0.7f) }
    var autoDeliver by remember { mutableStateOf(false) }
    var autoApprove by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Auto Mission") },
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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Search Configuration", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Job Query") },
                            placeholder = { Text("e.g., Python, React, Android") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        Text("Platforms", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("remoteok", "mostaql", "khamsat", "freelancer", "weworkremotely").forEach { platform ->
                                val isSelected = platform in selectedPlatforms
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            selectedPlatforms = selectedPlatforms - platform
                                        } else {
                                            selectedPlatforms = selectedPlatforms + platform
                                        }
                                    },
                                    label = { Text(platform) },
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Budget Range (USD)", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextField(
                                value = minBudget.toString(),
                                onValueChange = { minBudget = it.toIntOrNull() ?: 0 },
                                label = { Text("Min") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                value = maxBudget.toString(),
                                onValueChange = { maxBudget = it.toIntOrNull() ?: 5000 },
                                label = { Text("Max") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quality Threshold: ${(qualityThreshold * 100).toInt()}%", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Slider(
                            value = qualityThreshold,
                            onValueChange = { qualityThreshold = it },
                            valueRange = 0.0f..1.0f,
                            steps = 10,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Switch(
                            checked = autoDeliver,
                            onCheckedChange = { autoDeliver = it },
                            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End),
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text("Auto-deliver on quality gate pass")
                        }

                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Switch(
                            checked = autoApprove,
                            onCheckedChange = { autoApprove = it },
                            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End),
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text("Auto-approve proposals")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                isRunning = true
                                viewModel.startAutoMission(
                                    query = query,
                                    platforms = selectedPlatforms,
                                    minBudget = minBudget,
                                    maxBudget = maxBudget,
                                    qualityThreshold = qualityThreshold,
                                    autoDeliver = autoDeliver,
                                    autoApprove = autoApprove
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isRunning && query.isNotBlank(),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (query.isBlank()) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isRunning) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.padding(start = 12.dp))
                                    Text("Running Mission...", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            } else {
                                Text("Start Auto Mission", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }

                        if (isRunning && uiState.autoMissionStatus.isNotBlank()) {
                            Spacer(modifier = Modifier.padding(top = 16.dp))
                            Text(
                                text = uiState.autoMissionStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}