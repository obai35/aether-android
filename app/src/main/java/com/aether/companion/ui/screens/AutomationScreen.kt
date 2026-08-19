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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.aether.companion.R
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedPlatforms by remember { mutableStateOf<List<String>>(listOf("remoteok", "mostaql", "khamsat")) }
    var minBudget by remember { mutableStateOf(100) }
    var maxBudget by remember { mutableStateOf(5000) }
    var qualityThreshold by remember { mutableStateOf(0.7f) }
    var autoDeliver by remember { mutableStateOf(true) }
    var autoApprove by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val availablePlatforms = listOf("remoteok", "mostaql", "khamsat", "freelancer", "weworkremotely", "remotive")

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
                            availablePlatforms.forEach { platform ->
                                val isSelected = platform in selectedPlatforms
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPlatforms = if (isSelected) {
                                        selectedPlatforms - platform
                                    } else {
                                        selectedPlatforms + platform
                                    } },
                                    label = { Text(platform.capitalize()) },
                                    modifier = Modifier.weight(1f)
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
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                value = maxBudget.toString(),
                                onValueChange = { maxBudget = it.toIntOrNull() ?: 5000 },
                                label = { Text("Max") },
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
                            valueRange = 0f..1f,
                            steps = 10,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Delivery Options", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Auto Deliver")
                            Switch(
                                checked = autoDeliver,
                                onCheckedChange = { autoDeliver = it },
                            )
                        }

                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Auto Approve")
                            Switch(
                                checked = autoApprove,
                                onCheckedChange = { autoApprove = it },
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
                        Button(
                            onClick = {
                                isLoading = true
                                viewModel.startAutoMission(
                                    query = query,
                                    platforms = selectedPlatforms,
                                    minBudget = minBudget,
                                    maxBudget = maxBudget,
                                    qualityThreshold = qualityThreshold,
                                    autoDeliver = autoDeliver,
                                    autoApprove = autoApprove
                                )
                                isLoading = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Start Auto Mission", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}