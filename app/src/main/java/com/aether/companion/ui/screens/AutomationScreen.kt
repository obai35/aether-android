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
import androidx.compose.material3.CircularProgressIndicator
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
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun AutomationScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedPlatforms by remember { mutableStateOf(listOf("remoteok", "mostaql")) }
    var skills by remember { mutableStateOf("") }
    var maxProposals by remember { mutableStateOf(3) }
    var language by remember { mutableStateOf("python") }
    var qualityThreshold by remember { mutableStateOf("high") }
    var autoDeliver by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(false) }

    val platforms = listOf("remoteok", "remotive", "freelancer", "weworkremotely", "mostaql", "khamsat")
    val languages = listOf("python", "javascript", "typescript", "go", "rust")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Auto Mission") },
            navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(painterResource(R.drawable.ic_arrow_back), "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Configure Auto Mission", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    // Search Query
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search Query") },
                        placeholder = { Text("e.g., python web scraping") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Platforms
                    androidx.compose.foundation.layout.Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Platforms", fontWeight = FontWeight.Medium)
                        platforms.forEach { platform ->
                            androidx.material3.Checkbox(
                                checked = platform in selectedPlatforms,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedPlatforms = selectedPlatforms + platform
                                    } else {
                                        selectedPlatforms = selectedPlatforms.filter { it != platform }
                                    }
                                }
                            ) {
                                Text(platform.capitalize())
                            }
                        }
                    }

                    // Skills
                    TextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Your Skills (comma-separated)") },
                        placeholder = { Text("e.g., Python, Web Scraping, Automation") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Settings Row
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Max Proposals: $maxProposals", fontWeight = FontWeight.Medium)
                            Slider(
                                value = maxProposals.toFloat(),
                                onValueChange = { maxProposals = it.roundToInt() },
                                valueRange = 1f..10f,
                                steps = 9
                            )
                        }
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Language", fontWeight = FontWeight.Medium)
                            androidx.material3.Menu(
                                selectedValue = language,
                                onValueChange = { language = it },
                                items = languages
                            ) { lang ->
                                Text(lang.capitalize())
                            }
                        }
                    }

                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Quality Threshold", fontWeight = FontWeight.Medium)
                            androidx.material3.Menu(
                                selectedValue = qualityThreshold,
                                onValueChange = { qualityThreshold = it },
                                items = listOf("low", "medium", "high")
                            ) { threshold ->
                                Text(threshold.capitalize())
                            }
                        }
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            androidx.compose.foundation.layout.Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Switch(
                                    checked = autoDeliver,
                                    onCheckedChange = { autoDeliver = it }
                                )
                                Text("Auto Deliver")
                            }
                        }
                    }

                    // Start Button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (query.isNotBlank() && selectedPlatforms.isNotEmpty() && !isRunning) {
                                isRunning = true
                                viewModel.triggerAutoMission(
                                    com.aether.companion.data.api.AutoMissionRequest(
                                        query = query,
                                        platforms = selectedPlatforms,
                                        skills = skills.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                        maxJobs = 20,
                                        maxProposals = maxProposals,
                                        language = language,
                                        qualityThreshold = qualityThreshold,
                                        autoDeliver = autoDeliver
                                    )
                                )
                                isRunning = false
                            }
                        },
                        enabled = query.isNotBlank() && selectedPlatforms.isNotEmpty() && !isRunning
                    ) {
                        if (isRunning) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(end = 8.dp))
                        }
                        Text(if (isRunning) "Starting Mission..." else "Start Auto Mission")
                    }
                }
            }

            // Mission Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Mission Status", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Real-time updates will appear here when a mission is running")
                    // TODO: Add real-time mission progress display
                }
            }
        }
    }
}