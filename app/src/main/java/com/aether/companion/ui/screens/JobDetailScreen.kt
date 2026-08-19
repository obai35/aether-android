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
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun JobDetailScreen(
    viewModel: FreelancerViewModel = viewModel(),
    jobId: String,
    onNavigateBack: () -> Unit = {}
) {
    val job by viewModel.jobs.collectAsStateWithLifecycle()
        .map { it.find { it.id == jobId } }
    val events by viewModel.jobEvents.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    val jobEvents = events.getOrDefault(jobId, emptyList())

    if (job == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
            Text("Loading job details...")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job.title, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Job Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(job.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        JobStatusChip(status = job.status)
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                    Text("Platform: ${job.platform}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Budget: \$${job.minBudget} - \$${job.maxBudget}", fontWeight = FontWeight.Medium)
                    Text("Language: ${job.language}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Progress: ${job.progress}%", color = MaterialTheme.colorScheme.onSurfaceVariant)

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                    androidx.compose.foundation.layout.Row(
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
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
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
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(job.proposal ?: "No proposal yet")
            }
        }

        if (job.files.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Files", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(job.files) { file ->
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(file)
                                Icon(Icons.Default.Description, contentDescription = file)
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
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                androidx.compose.material3.LinearProgressIndicator(
                    progress = job.progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                Text("Stage: ${job.status.name}", fontWeight = FontWeight.Medium)
                Text("Attempts: ${job.attempts}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (job.testResult.isNotBlank()) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Test Result: ${job.testResult}", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
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
fun QualityTab(job: FreelancerJob, uiState: com.aether.companion.ui.viewmodel.UIState) {
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
                Text("Quality Gate", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                val qualityResult = uiState.qualityGateResults[job.id]
                if (qualityResult != null) {
                    QualityGateRow(title = "Lint", passed = qualityResult.lintPassed)
                    QualityGateRow(title = "Security", passed = qualityResult.securityPassed)
                    QualityGateRow(title = "Tests", passed = qualityResult.testsPassed)

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                    val allPassed = qualityResult.lintPassed && qualityResult.securityPassed && qualityResult.testsPassed
                    Text(
                        if (allPassed) "All quality gates passed ✓" else "Some quality gates failed ✗",
                        fontWeight = FontWeight.Bold,
                        color = if (allPassed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("No quality gate results yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                    Button(onClick = { viewModel.runQualityGate(job.id) }) {
                        Text("Run Quality Gate")
                    }
                }
            }
        }
    }
}

@Composable
fun QualityGateRow(title: String, passed: Boolean) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Icon(
            if (passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = if (passed) "Passed" else "Failed",
            tint = if (passed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
        )
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("All Events", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                if (events.isEmpty()) {
                    Text("No events yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
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
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(event.type, fontWeight = FontWeight.Medium)
            Text(event.timestamp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Text(event.details ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun JobStatusChip(status: FreelancerJob.JobStatus) {
    val (color, text) = when (status) {
        FreelancerJob.JobStatus.PENDING -> MaterialTheme.colorScheme.outline to "Pending"
        FreelancerJob.JobStatus.SEARCHING -> MaterialTheme.colorScheme.primary to "Searching"
        FreelancerJob.JobStatus.WORKING -> MaterialTheme.colorScheme.secondary to "Working"
        FreelancerJob.JobStatus.AWAITING_APPROVAL -> MaterialTheme.colorScheme.tertiary to "Awaiting Approval"
        FreelancerJob.JobStatus.IMPLEMENTED -> MaterialTheme.colorScheme.primary to "Implemented"
        FreelancerJob.JobStatus.DELIVERED -> MaterialTheme.colorScheme.tertiary to "Delivered"
        FreelancerJob.JobStatus.FAILED -> MaterialTheme.colorScheme.error to "Failed"
        FreelancerJob.JobStatus.COMPLETED -> MaterialTheme.colorScheme.primary to "Completed"
    }

    androidx.compose.material3.Chip(
        modifier = Modifier.wrapContentSize(),
        onClick = {},
        label = { Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp) },
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.2f)
        )
    )
}

val TabTitles = listOf("Overview", "Progress", "Quality", "Events")