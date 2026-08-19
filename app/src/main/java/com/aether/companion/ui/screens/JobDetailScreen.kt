package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aether.companion.R
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.components.JobStatusChip
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun JobDetailScreen(
    viewModel: FreelancerViewModel = viewModel(),
    jobId: String,
    onNavigateBack: () -> Unit = {}
) {
    val job by viewModel.getJob(jobId).collectAsStateWithLifecycle()
    val jobEvents by viewModel.getJobEvents(jobId).collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    val TabTitles = listOf("Overview", "Progress", "Quality", "Events")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportPackage(jobId, false) }) {
                        Icon(Icons.Default.Download, contentDescription = "Export English")
                    }
                    IconButton(onClick = { viewModel.exportPackage(jobId, true) }) {
                        Icon(Icons.Default.Language, contentDescription = "Export Arabic")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Job Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(modifier = Modifier.padding(top = 4.dp))
                                Text(job.platform, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            JobStatusChip(status = job.status)
                        }
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Text("Budget: \$${job.minBudget} - \$${job.maxBudget}", fontWeight = FontWeight.Medium)
                        Text("Language: ${job.language}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Progress: ${job.progress}%", color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { viewModel.approveProposal(jobId) }, enabled = job.status == FreelancerJob.JobStatus.AWAITING_APPROVAL) {
                                Text("Approve")
                            }
                            Button(onClick = { viewModel.deliverJob(jobId) }, enabled = job.status == FreelancerJob.JobStatus.IMPLEMENTED) {
                                Text("Deliver")
                            }
                            Button(onClick = { viewModel.exportPackage(jobId, false) }) {
                                Text("Export EN")
                            }
                            Button(onClick = { viewModel.exportPackage(jobId, true) }) {
                                Text("Export AR")
                            }
                        }
                    }
                }

                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    tabs = { TabTitles.forEachIndexed { index, title ->
                        androidx.compose.material3.Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }},
                    indicator = { tabPositions ->
                        androidx.compose.foundation.layout.Box(
                            Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationX = tabPositions[selectedTab].left
                                    scaleX = (tabPositions[selectedTab].right - tabPositions[selectedTab].left) / size.width
                                }
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    },
                    divider = { androidx.compose.foundation.layout.Box(Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outlineVariant)) },
                    contentColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                )

                // Tab Content
                when (selectedTab) {
                    0 -> OverviewTab(job = job)
                    1 -> ProgressTab(job = job, events = jobEvents)
                    2 -> QualityTab(job = job, uiState = uiState)
                    3 -> EventsTab(events = jobEvents)
                    else -> OverviewTab(job = job)
                }
            }
        }
    )
}

@Composable
fun OverviewTab(job: FreelancerJob) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Requirements", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(job.requirements)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Proposal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(job.proposal ?: "No proposal yet")
            }
        }

        if (job.files != null && job.files!!.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Files", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(job.files!!) { file ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(file.key)
                                Icon(Icons.Default.Description, contentDescription = file.key)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressTab(job: FreelancerJob, events: List<AutomationEvent>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Progress: ${job.progress}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(top = 16.dp))

                androidx.compose.material3.LinearProgressIndicator(
                    progress = job.progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Text("Stage: ${job.status.name}", fontWeight = FontWeight.Medium)
                Text("Attempts: ${job.attempts}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (job.testResult != null && job.testResult!!.output.isNotBlank()) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Test Result: ${job.testResult!!.output}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (events.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Timeline", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(events) { event ->
                            EventRow(event = event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QualityTab(job: FreelancerJob, uiState: com.aether.companion.ui.viewmodel.FreelancerViewModel.UIState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (job.qualityGate != null) {
            val qg = job.qualityGate!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (qg.passed) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Quality Gate", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (qg.passed) {
                            Chip(
                                onClick = {},
                                colors = ChipDefaults.chipColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("PASSED", fontSize = 12.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                        } else {
                            Chip(
                                onClick = {},
                                colors = ChipDefaults.chipColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("FAILED", fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Score: ${String.format("%.1f", qg.score * 100)}%", fontWeight = FontWeight.Medium)

                    if (qg.details.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(qg.details) { detail ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(detail.key)
                                    Text(detail.value, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "No quality gate", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), size = 48.dp)
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    Text("No quality gate run yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (job.review != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Code Review", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(job.review!!.summary)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Score: ${job.review!!.score}/10", fontWeight = FontWeight.Medium)
                    if (job.review!!.issues.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text("Issues:", fontWeight = FontWeight.Medium)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(job.review!!.issues) { issue ->
                                Text("• $issue")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventsTab(events: List<AutomationEvent>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (events.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, contentDescription = "No events", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), size = 48.dp)
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    Text("No events yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("All Events", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(events) { event ->
                            EventRow(event = event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventRow(event: AutomationEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(event.type, fontWeight = FontWeight.Medium)
                Text(event.timestamp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(event.details ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

val TabTitles = listOf("Overview", "Progress", "Quality", "Events")