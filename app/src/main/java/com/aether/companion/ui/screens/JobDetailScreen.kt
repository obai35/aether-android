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

    val events by viewModel.automationEvents.collectAsStateWithLifecycle()
        .map { it.filter { it.jobId == jobId } }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Progress", "Quality", "Deliverable", "Events")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { job?.let { Text(it.title, maxLines = 1) } ?: Text("Job Detail") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                indicatorColor = MaterialTheme.colorScheme.primary,
                dividerColor = MaterialTheme.colorScheme.outlineVariant
            ) {
                tabs.forEachIndexed { index, title ->
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp),
                        fontWeight = if (index == selectedTab) FontWeight.Bold else FontWeight.Normal,
                        color = if (index == selectedTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> OverviewTab(job)
                1 -> ProgressTab(job, events)
                2 -> QualityTab(job)
                3 -> DeliverableTab(viewModel, job)
                4 -> EventsTab(events)
            }

            // Action Buttons based on job status
            job?.let { j ->
                JobActionButtons(viewModel, j)
            }
        }
    }
}

@Composable
fun OverviewTab(job: FreelancerJob?) {
    job?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Platform", it.platform)
                DetailRow("Source", it.source)
                DetailRow("Language", it.language)
                DetailRow("Skill Match", it.skillScore?.let { "%.1f%%".format(it * 100) } ?: "N/A")
                DetailRow("Attempts", it.attempts.toString())
                DetailRow("Created", java.text.SimpleDateFormat("MMM dd, HH:mm").format(java.util.Date(it.createdAt)))
                DetailRow("Updated", java.text.SimpleDateFormat("MMM dd, HH:mm").format(java.util.Date(it.updatedAt)))

                if (it.requirements.isNotBlank()) {
                    androidx.compose.foundation.layout.Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Requirements", fontWeight = FontWeight.Bold)
                        Text(it.requirements, maxLines = 5, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }

                if (it.plan != null && it.plan!!.isNotBlank()) {
                    androidx.compose.foundation.layout.Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Implementation Plan", fontWeight = FontWeight.Bold)
                        Text(it.plan!!, maxLines = 5, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressTab(job: FreelancerJob?, events: List<AutomationEvent>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Progress indicator
        job?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Current Status", fontWeight = FontWeight.Bold)
                    JobStatusChip(it.status)
                    Text(it.progress, fontSize = 14.sp)
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = getProgressForStatus(it.status),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Timeline
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Timeline", fontWeight = FontWeight.Bold)
                events.sortedBy { it.timestamp }.forEach { event ->
                    TimelineItem(event)
                }
            }
        }
    }
}

@Composable
fun TimelineItem(event: AutomationEvent) {
    val time = java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(event.timestamp))
    val (icon, color) = when (event.type) {
        AutomationEvent.EventType.JOB_STARTED -> R.drawable.ic_play_arrow to Color.Blue
        AutomationEvent.EventType.STAGE_CHANGED -> R.drawable.ic_sync to Color.Purple
        AutomationEvent.EventType.QUALITY_GATE_STARTED -> R.drawable.ic_security to Color.Orange
        AutomationEvent.EventType.QUALITY_GATE_COMPLETED -> R.drawable.ic_check_circle to Color.Green
        AutomationEvent.EventType.HUMAN_REQUIRED -> R.drawable.ic_warning to Color.Red
        AutomationEvent.EventType.JOB_COMPLETED -> R.drawable.ic_done_all to Color.Green
        AutomationEvent.EventType.JOB_FAILED -> R.drawable.ic_error to Color.Red
        AutomationEvent.EventType.PROPOSAL_GENERATED -> R.drawable.ic_description to Color.Blue
        AutomationEvent.EventType.DELIVERABLE_READY -> R.drawable.ic_download to Color.Green
        else -> R.drawable.ic_info to Color.Gray
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(painterResource(icon), null, tint = color, modifier = Modifier.size(24.dp))
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(event.type.value.replace("_", " ").capitalize(), fontWeight = FontWeight.Medium)
            Text(time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            (event.data as? AutomationEvent.EventData.ProgressData)?.message?.let {
                Text(it, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun QualityTab(job: FreelancerJob?) {
    job?.qualityGate?.let { gate ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Quality Gate", fontWeight = FontWeight.Bold)
                    Text(if (gate.passed) "PASSED" else "FAILED", color = if (gate.passed) Color.Green else Color.Red, fontWeight = FontWeight.Bold)
                }
                Text("Threshold: ${gate.threshold}")

                gate.blockingIssues.forEach { issue ->
                    androidx.compose.material3.Chip(
                        onClick = {},
                        colors = androidx.material3.ChipDefaults.chipColors(containerColor = Color.Red.copy(alpha = 0.1f))
                    ) {
                        Text(issue, color = Color.Red, fontSize = 12.sp)
                    }
                }

                gate.checks.forEach { (name, check) ->
                    DetailRow(
                        name.capitalize(),
                        if (check.passed == true) "✓ Passed" else if (check.returnCode != null && check.returnCode != 0) "✗ Failed (code: ${check.returnCode})" else "⚠ Warning"
                    )
                    check.output?.let {
                        Text(it, fontSize = 10.sp, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    } ?: Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No quality gate data yet")
        }
    }
}

@Composable
fun DeliverableTab(viewModel: FreelancerViewModel, job: FreelancerJob?) {
    job?.let {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Export Deliverable", fontWeight = FontWeight.Bold)
                    Text("Generate a client-ready package with code, tests, README, proposal, and quality report")
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.exportPackage(it.id, arabic = false)
                            }
                        ) {
                            Text("Export English Package")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.exportPackage(it.id, arabic = true)
                            }
                        ) {
                            Text("Export Arabic Package")
                        }
                    }
                }
            }

            if (it.status == FreelancerJob.JobStatus.IMPLEMENTED || it.status == FreelancerJob.JobStatus.DELIVERED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Confirm Delivery", fontWeight = FontWeight.Bold)
                        Text("Mark this job as delivered to the client")
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.confirmDelivery(it.id)
                            }
                        ) {
                            Text("Confirm Delivery")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventsTab(events: List<AutomationEvent>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events.sortedByDescending { it.timestamp }) { event ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(event.type.value.replace("_", " ").capitalize(), fontWeight = FontWeight.Medium)
                        Text(java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(event.timestamp)))
                    }
                    (event.data as? AutomationEvent.EventData.ProgressData)?.message?.let {
                        Text(it, fontSize = 14.sp)
                    }
                    (event.data as? AutomationEvent.EventData.StageData)?.details?.let { details ->
                        details.forEach { (k, v) ->
                            Text("$k: $v", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    (event.data as? AutomationEvent.EventData.HumanRequiredData)?.let { human ->
                        androidx.compose.material3.Chip(
                            onClick = {},
                            colors = androidx.material3.ChipDefaults.chipColors(containerColor = Color.Red.copy(alpha = 0.1f))
                        ) {
                            Text("Action: ${human.actionRequired.value}", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobActionButtons(viewModel: FreelancerViewModel, job: FreelancerJob) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (job.status) {
            FreelancerJob.JobStatus.AWAITING_APPROVAL -> {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.approveProposal(job.id) }
                ) {
                    Text("Approve Proposal")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { /* reject */ }
                ) {
                    Text("Reject")
                }
            }
            FreelancerJob.JobStatus.REQUIRES_HUMAN -> {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { /* handle based on action type */ }
                ) {
                    Text("Resolve")
                }
            }
            FreelancerJob.JobStatus.IMPLEMENTED -> {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.confirmDelivery(job.id) }
                ) {
                    Text("Deliver")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.exportPackage(job.id) }
                ) {
                    Text("Export")
                }
            }
            else -> {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.exportPackage(job.id) }
                ) {
                    Text("Export")
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

fun getProgressForStatus(status: FreelancerJob.JobStatus): Float {
    return when (status) {
        FreelancerJob.JobStatus.PENDING -> 0f
        FreelancerJob.JobStatus.SEARCHING -> 0.1f
        FreelancerJob.JobStatus.WORKING -> 0.5f
        FreelancerJob.JobStatus.AWAITING_APPROVAL -> 0.7f
        FreelancerJob.JobStatus.IMPLEMENTED -> 0.9f
        FreelancerJob.JobStatus.DELIVERED -> 1f
        FreelancerJob.JobStatus.FAILED -> 0f
        FreelancerJob.JobStatus.NEEDS_FIXES -> 0.6f
        FreelancerJob.JobStatus.QUALITY_GATE_FAILED -> 0.8f
        FreelancerJob.JobStatus.REQUIRES_HUMAN -> 0.5f
    }
}