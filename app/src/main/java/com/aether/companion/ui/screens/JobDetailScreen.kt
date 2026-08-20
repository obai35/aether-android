package com.aether.companion.ui.screens

import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.EventType
import com.aether.companion.data.model.EventData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
    val job by viewModel.getJob(jobId).collectAsStateWithLifecycle(initialValue = null)
    val jobEvents by viewModel.getJobEvents(jobId).collectAsStateWithLifecycle(initialValue = emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    val TabTitles = listOf("Overview", "Progress", "Quality", "Events")

    if (job == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Job Details") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading job...")
                }
            }
        )
        return
    }

    // Copy to local vals to avoid smart cast issues - use !! since we checked job != null above
    val currentJob: FreelancerJob = job!!
    val jobTitle = currentJob.title
    val jobPlatform = currentJob.platform
    val jobSkillScore = currentJob.skillScore
    val jobLanguage = currentJob.language
    val jobProgress = currentJob.progress.toIntOrNull() ?: 0
    val jobStatus = currentJob.status
    val jobAttempts = currentJob.attempts
    val jobProposal = currentJob.proposal
    val jobRequirements = currentJob.requirements
    val jobPlan = currentJob.plan
    val jobQualityGate = currentJob.qualityGate
    val jobReview = currentJob.review
    val jobTestResult = currentJob.testResult

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(jobTitle) },
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
                                Text(jobTitle, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(modifier = Modifier.padding(top = 4.dp))
                                Text(jobPlatform, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            JobStatusChip(status = jobStatus)
                        }
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Text("Budget: \$${jobSkillScore?.let { String.format("%.0f", it) } ?: "0"} - \$${jobSkillScore?.let { String.format("%.0f", it) } ?: "0"}", fontWeight = FontWeight.Medium)
                        Text("Language: ${jobLanguage}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Progress: ${jobProgress}%", color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { viewModel.approveProposal(jobId) }, enabled = jobStatus == FreelancerJob.JobStatus.AWAITING_APPROVAL) {
                                Text("Approve")
                            }
                            Button(onClick = { viewModel.confirmDelivery(jobId) }, enabled = jobStatus == FreelancerJob.JobStatus.IMPLEMENTED) {
                                Text("Confirm Delivery")
                            }
                        }
                    }
                }

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                ) {
                    TabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> OverviewTab(
                        proposal = jobProposal,
                        requirements = jobRequirements,
                        plan = jobPlan
                    )
                    1 -> ProgressTab(
                        progress = jobProgress,
                        status = jobStatus,
                        attempts = jobAttempts,
                        testResult = jobTestResult,
                        events = jobEvents
                    )
                    2 -> QualityTab(
                        qualityGate = jobQualityGate,
                        review = jobReview
                    )
                    3 -> EventsTab(events = jobEvents)
                }
            }
        }
    )
}

@Composable
fun OverviewTab(
    proposal: String?,
    requirements: String,
    plan: String?
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (proposal != null && proposal.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Proposal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(proposal)
                }
            }
        }

        if (requirements.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Requirements", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(requirements)
                }
            }
        }

        if (plan != null && plan.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Plan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(plan)
                }
            }
        }
    }
}

@Composable
fun ProgressTab(
    progress: Int,
    status: FreelancerJob.JobStatus,
    attempts: Int,
    testResult: com.aether.companion.data.model.TestResult?,
    events: List<AutomationEvent>
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Progress: ${progress}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(top = 16.dp))

                androidx.compose.material3.LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Text("Stage: ${status.name}", fontWeight = FontWeight.Medium)
                Text("Attempts: ${attempts}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (testResult != null && testResult.output.isNotBlank()) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Test Result: ${testResult.output}", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun QualityTab(
    qualityGate: com.aether.companion.data.model.QualityGate?,
    review: com.aether.companion.data.model.CodeReview?
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (qualityGate != null) {
            val qg = qualityGate
            val passed = qg.passed
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (passed) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Quality Gate", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (passed) {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "PASSED",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "FAILED",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Lint: ${if (qg.lintPassed) "Passed" else "Failed"} | Security: ${if (qg.securityPassed) "Passed" else "Failed"} | Tests: ${if (qg.testsPassed) "Passed" else "Failed"}", fontWeight = FontWeight.Medium)

                    if (qg.issues.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(qg.issues) { issue ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${issue.type}: ${issue.message}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(issue.severity, color = when (issue.severity) {
                                        "critical", "high" -> MaterialTheme.colorScheme.error
                                        "medium" -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    })
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
                    Icon(Icons.Default.CheckCircle, contentDescription = "No quality gate", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    Text("No quality gate run yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (review != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Code Review", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(review.summary)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Score: ${review.score}/10", fontWeight = FontWeight.Medium)
                    if (review.issues.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text("Issues:", fontWeight = FontWeight.Medium)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(review.issues) { issue ->
                                Text("• ${issue.message} (${issue.file}:${issue.line})")
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
                Icon(Icons.Default.History, contentDescription = "No events", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.padding(top = 16.dp))
                Text("No events yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventRow(event = event)
        }
    }
}

@Composable
fun EventRow(event: AutomationEvent) {
    val (icon, color) = when (event.type) {
        com.aether.companion.data.model.EventType.STAGE_CHANGED -> Icons.Default.History to MaterialTheme.colorScheme.primary
        com.aether.companion.data.model.EventType.JOB_PROGRESS -> Icons.Default.PlayArrow to MaterialTheme.colorScheme.tertiary
        com.aether.companion.data.model.EventType.HUMAN_REQUIRED -> Icons.Default.Warning to MaterialTheme.colorScheme.error
        com.aether.companion.data.model.EventType.JOB_COMPLETED -> Icons.Default.CheckCircle to MaterialTheme.colorScheme.primary
        com.aether.companion.data.model.EventType.JOB_FAILED -> Icons.Default.Error to MaterialTheme.colorScheme.error
        com.aether.companion.data.model.EventType.QUALITY_GATE_STARTED,
        com.aether.companion.data.model.EventType.QUALITY_GATE_COMPLETED -> Icons.Default.FactCheck to MaterialTheme.colorScheme.secondary
        else -> Icons.Default.Info to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = event.type.name, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text(event.type.name, fontWeight = FontWeight.Medium)
                        val detailsText = when (event.data) {
                            is AutomationEvent.EventData.ProgressData -> event.data.message
                            is AutomationEvent.EventData.StageData -> event.data.stage
                            is AutomationEvent.EventData.QualityGateData -> if (event.data.passed) "Passed" else "Failed"
                            is AutomationEvent.EventData.HumanRequiredData -> event.data.reason
                            is AutomationEvent.EventData.JobCompletedData -> event.data.summary ?: "Completed"
                            is AutomationEvent.EventData.JobFailedData -> "Failed"
                            is AutomationEvent.EventData.LogData -> event.data.message
                            else -> ""
                        }
                        if (detailsText.isNotBlank()) {
                            Text(detailsText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Text(
                    java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date(event.timestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}