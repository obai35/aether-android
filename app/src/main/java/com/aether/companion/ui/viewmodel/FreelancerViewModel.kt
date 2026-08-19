package com.aether.companion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.AIMessage
import com.aether.companion.data.model.MessageRole
import com.aether.companion.data.model.QualityGateResult
import com.aether.companion.data.repository.FreelancerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.UUID

class FreelancerViewModel(
    private val repository: FreelancerRepository
) : ViewModel() {

    // State flows
    val jobs = repository.jobs
    val automationEvents = repository.automationEvents
    val currentEvent = repository.currentEvent
    val isConnected = repository.isConnected
    val stats = repository.stats

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UIState.Loading)

    private val _assistantMessages = MutableStateFlow<List<AIMessage>>(emptyList())
    val assistantMessages = _assistantMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _pendingHumanAction = MutableStateFlow<AutomationEvent?>(null)
    val pendingHumanAction = _pendingHumanAction.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        loadInitialData()
        observeEvents()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                repository.refreshJobs()
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Failed to load jobs")
            }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            automationEvents.collectLatest { events ->
                events.lastOrNull()?.let { event ->
                    when (event.type) {
                        AutomationEvent.EventType.HUMAN_REQUIRED -> {
                            _pendingHumanAction.value = event
                        }
                        AutomationEvent.EventType.JOB_COMPLETED -> {
                            repository.refreshJobs()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun sendAssistantMessage(message: String) {
        val userMessage = AIMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = message,
            timestamp = System.currentTimeMillis()
        )
        _assistantMessages.value = _assistantMessages.value + userMessage

        viewModelScope.launch {
            // TODO: Call API
            val responseMessage = AIMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                content = "Received: $message (API integration pending)",
                timestamp = System.currentTimeMillis()
            )
            _assistantMessages.value = _assistantMessages.value + responseMessage
        }
    }

    fun approveProposal(jobId: String) {
        viewModelScope.launch {
            try {
                repository.approveProposal(jobId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun confirmDelivery(jobId: String) {
        viewModelScope.launch {
            try {
                repository.confirmDelivery(jobId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun startAutoMission(
        query: String,
        platforms: List<String>,
        minBudget: Int,
        maxBudget: Int,
        qualityThreshold: Float,
        autoDeliver: Boolean,
        autoApprove: Boolean
    ) {
        _uiState.value = UIState.Success
        viewModelScope.launch {
            try {
                repository.startAutoMission(
                    query = query,
                    platforms = platforms,
                    minBudget = minBudget,
                    maxBudget = maxBudget,
                    qualityThreshold = qualityThreshold,
                    autoDeliver = autoDeliver,
                    autoApprove = autoApprove
                )
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Failed to start auto mission")
            }
        }
    }

    fun runQualityGate(jobId: String) {
        viewModelScope.launch {
            try {
                val result = repository.runQualityGate(jobId)
                val currentState = _uiState.value
                if (currentState is UIState.Success) {
                    val updatedResults = currentState.qualityGateResults + (jobId to result)
                    _uiState.value = currentState.copy(qualityGateResults = updatedResults)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateSettings(apiUrl: String, apiKey: String) {
        viewModelScope.launch {
            try {
                repository.updateSettings(apiUrl, apiKey)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    sealed interface UIState {
        data class Loading : UIState
        data class Success(
            val assistantMessages: List<AIMessage> = emptyList(),
            val isLoading: Boolean = false,
            val autoMissionStatus: String = "",
            val qualityGateResults: Map<String, QualityGateResult> = emptyMap()
        ) : UIState
        data class Error(val message: String) : UIState
    }
}