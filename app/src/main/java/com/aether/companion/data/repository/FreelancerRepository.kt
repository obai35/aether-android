package com.aether.companion.data.repository

import com.aether.companion.data.api.AetherApiService
import com.aether.companion.data.api.AetherWebSocketClient
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.AIAssistant.AIAssistantRequest
import com.aether.companion.data.model.AIAssistant.AIAssistantResponse
import com.aether.companion.data.model.AIAssistant.AssistantAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import retrofit2.Response

class FreelancerRepository(
    private val apiService: AetherApiService,
    private val webSocketClient: AetherWebSocketClient
) {
    private val _jobs = MutableStateFlow<List<FreelancerJob>>(emptyList())
    val jobs = _jobs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _automationEvents = MutableStateFlow<List<AutomationEvent>>(emptyList())
    val automationEvents = _automationEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _currentEvent = MutableStateFlow<AutomationEvent?>(null)
    val currentEvent = _currentEvent.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _stats = MutableStateFlow<FreelancerStats?>(null)
    val stats = _stats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        setupWebSocket()
    }

    private fun setupWebSocket() {
        webSocketClient.connect()
        _isConnected.value = webSocketClient.isConnected()
    }

    suspend fun refreshJobs(status: FreelancerJob.JobStatus? = null) {
        val response = apiService.getJobs(status = status)
        if (response.isSuccessful) {
            response.body()?.let { _jobs.value = it }
        }
    }

    suspend fun getJob(jobId: String): FreelancerJob? {
        val response = apiService.getJob(jobId)
        return response.body()
    }

    suspend fun approveProposal(jobId: String): Boolean {
        val response = apiService.approveProposal(jobId)
        return response.isSuccessful && response.body()?.success == true
    }

    suspend fun confirmDelivery(jobId: String): DeliveryResult {
        val response = apiService.confirmDelivery(jobId)
        if (response.isSuccessful) {
            val body = response.body()
            return DeliveryResult(body?.success == true, body?.message, body?.deliverableUrl)
        }
        return DeliveryResult(false, response.message(), null)
    }

    suspend fun exportPackage(jobId: String, arabic: Boolean = false): ExportResult {
        val response = if (arabic) {
            apiService.exportArabicPackage(jobId)
        } else {
            apiService.exportPackage(jobId)
        }
        if (response.isSuccessful) {
            val body = response.body()
            return ExportResult(
                body?.success == true,
                body?.packagePath,
                body?.packageName,
                body?.sizeBytes
            )
        }
        return ExportResult(false, null, null, null)
    }

    suspend fun chatWithAssistant(request: AIAssistantRequest): AIAssistantResponse? {
        val response = apiService.chatWithAssistant(request)
        return response.body()
    }

    suspend fun refreshStats() {
        val response = apiService.getStats()
        if (response.isSuccessful) {
            _stats.value = response.body()
        }
    }

    suspend fun triggerAutoMission(request: AutoMissionRequest): AutoMissionResult {
        val response = apiService.triggerAutoMission(request)
        if (response.isSuccessful) {
            val body = response.body()
            return AutoMissionResult(body?.success == true, body?.missionId, body?.message)
        }
        return AutoMissionResult(false, null, response.message())
    }

    fun onEventReceived(event: AutomationEvent) {
        _automationEvents.value = (_automationEvents.value + event).takeLast(100)
        _currentEvent.value = event

        // Update job status if event contains job info
        when (event.type) {
            AutomationEvent.EventType.JOB_COMPLETED,
            AutomationEvent.EventType.JOB_FAILED,
            AutomationEvent.EventType.QUALITY_GATE_COMPLETED -> {
                refreshJobs()
                refreshStats()
            }
        }
    }

    fun disconnect() {
        webSocketClient.disconnect()
        _isConnected.value = false
    }
}

data class DeliveryResult(
    val success: Boolean,
    val message: String?,
    val deliverableUrl: String?
)

data class ExportResult(
    val success: Boolean,
    val packagePath: String?,
    val packageName: String?,
    val sizeBytes: Long?
)

data class AutoMissionResult(
    val success: Boolean,
    val missionId: String?,
    val message: String?
)

// Extension to access viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

// Note: In real implementation, inject viewModelScope from ViewModel
// This is a placeholder - actual implementation would use ViewModel's viewModelScope
private val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob())