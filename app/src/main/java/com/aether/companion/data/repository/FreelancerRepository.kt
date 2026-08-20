package com.aether.companion.data.repository

import com.aether.companion.data.api.AetherApiService
import com.aether.companion.data.api.AutoMissionRequest
import com.aether.companion.data.api.AutoMissionResponse
import com.aether.companion.data.api.AIAssistantRequest
import com.aether.companion.data.api.AIAssistantResponse
import com.aether.companion.data.api.SettingsRequest
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.FreelancerJob.JobStatus
import com.aether.companion.data.model.QualityGateResult
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import retrofit2.Response

class FreelancerRepository(
    private val apiService: AetherApiService,
    private val scope: CoroutineScope
) {
    private val _jobs = MutableStateFlow<List<FreelancerJob>>(emptyList())
    val jobs = _jobs.stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    private val _automationEvents = MutableStateFlow<List<AutomationEvent>>(emptyList())
    val automationEvents = _automationEvents.stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    private val _currentEvent = MutableStateFlow<AutomationEvent?>(null)
    val currentEvent = _currentEvent.stateIn(scope, SharingStarted.WhileSubscribed(), null)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.stateIn(scope, SharingStarted.WhileSubscribed(), false)

    private val _stats = MutableStateFlow<FreelancerStats?>(null)
    val stats = _stats.stateIn(scope, SharingStarted.WhileSubscribed(), null)

    suspend fun refreshJobs(status: JobStatus? = null) {
        try {
            val response = apiService.getJobs(status = status)
            if (response.isSuccessful && response.body() != null) {
                _jobs.value = response.body()!!
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun getJob(jobId: String): FreelancerJob? {
        try {
            val response = apiService.getJob(jobId)
            return if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun approveProposal(jobId: String): Boolean {
        try {
            val response = apiService.approveProposal(jobId)
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun confirmDelivery(jobId: String): Boolean {
        try {
            val response = apiService.confirmDelivery(jobId)
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun sendAssistantMessage(message: String): String {
        try {
            val request = com.aether.companion.data.api.AIAssistantRequest(message = message)
            val response = apiService.chatWithAssistant(request)
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.response
            } else {
                "Error: Failed to get response"
            }
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }

    suspend fun exportPackage(jobId: String): String? {
        try {
            val response = apiService.exportPackage(jobId)
            return if (response.isSuccessful) response.body()?.downloadUrl else null
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun startAutoMission(
        query: String,
        platforms: List<String>,
        minBudget: Int,
        maxBudget: Int,
        qualityThreshold: Float,
        autoDeliver: Boolean,
        autoApprove: Boolean
    ): AutoMissionResponse {
        val request = AutoMissionRequest(
            query = query,
            platforms = platforms,
            minBudget = minBudget,
            maxBudget = maxBudget,
            qualityThreshold = qualityThreshold,
            autoDeliver = autoDeliver,
            autoApprove = autoApprove
        )
        try {
            val response = apiService.startAutoMission(request)
            return if (response.isSuccessful) response.body()!! else AutoMissionResponse(false, null, "API error")
        } catch (e: Exception) {
            return AutoMissionResponse(false, null, e.message)
        }
    }

    suspend fun runQualityGate(jobId: String): QualityGateResult {
        try {
            val response = apiService.runQualityGate(jobId)
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                QualityGateResult()
            }
        } catch (e: Exception) {
            return QualityGateResult()
        }
    }

    suspend fun updateSettings(apiUrl: String, apiKey: String) {
        try {
            val request = SettingsRequest(apiUrl, apiKey)
            apiService.updateSettings(request)
        } catch (e: Exception) {
            // Handle error
        }
    }

    data class FreelancerStats(
        val totalJobs: Int,
        val completedJobs: Int,
        val pendingJobs: Int,
        val failedJobs: Int,
        val totalEarnings: Double
    )

    data class ExportResponse(
        val success: Boolean,
        val downloadUrl: String?,
        val expiresAt: Long?
    )

    data class AutoMissionResult(
        val success: Boolean,
        val missionId: String?,
        val message: String?
    )
}