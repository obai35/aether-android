package com.aether.companion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.AIAssistant.AIAssistantRequest
import com.aether.companion.data.model.AIAssistant.AIMessage
import com.aether.companion.data.model.AIAssistant.MessageRole
import com.aether.companion.data.repository.FreelancerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class FreelancerViewModel @Inject constructor(
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
            repository.refreshJobs()
            repository.refreshStats()
            _uiState.value = UIState.Success
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            automationEvents.collect { events ->
                events.lastOrNull()?.let { event ->
                    when (event.type) {
                        AutomationEvent.EventType.HUMAN_REQUIRED -> {
                            _pendingHumanAction.value = event
                            _uiState.value = UIState.HumanActionRequired(event)
                        }
                        AutomationEvent.EventType.JOB_COMPLETED -> {
                            _uiState.value = UIState.JobCompleted(event.data as? AutomationEvent.EventData.JobCompletedData)
                        }
                        AutomationEvent.EventType.JOB_FAILED -> {
                            _uiState.value = UIState.Error(event.data.toString())
                        }
                        else -> {
                            // Update UI state based on event
                        }
                    }
                }
            }
        }
    }

    fun refreshJobs() {
        viewModelScope.launch {
            repository.refreshJobs()
        }
    }

    suspend fun approveProposal(jobId: String): Boolean {
        return repository.approveProposal(jobId)
    }

    suspend fun confirmDelivery(jobId: String): DeliveryResult {
        return repository.confirmDelivery(jobId)
    }

    suspend fun exportPackage(jobId: String, arabic: Boolean = false): ExportResult {
        return repository.exportPackage(jobId, arabic)
    }

    suspend fun sendAssistantMessage(message: String) {
        val userMessage = AIMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = message,
            timestamp = System.currentTimeMillis(),
            toolCalls = null,
            toolCallId = null,
            metadata = null
        )
        _assistantMessages.value = _assistantMessages.value + userMessage

        val request = AIAssistantRequest(
            messages = _assistantMessages.value,
            tools = getAvailableTools(),
            context = null
        )

        viewModelScope.launch {
            val response = repository.chatWithAssistant(request)
            response?.message?.let { assistantMessage ->
                _assistantMessages.value = _assistantMessages.value + assistantMessage
                // Execute any actions
                response.actions?.forEach { action ->
                    executeAssistantAction(action)
                }
            }
        }
    }

    private fun getAvailableTools(): List<ToolDefinition> {
        return listOf(
            ToolDefinition(
                type = "function",
                function = FunctionSchema(
                    name = "get_job_status",
                    description = "Get current status of a freelancer job",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "job_id" to mapOf("type" to "string", "description" to "Job ID")
                        ),
                        "required" to listOf("job_id")
                    )
                )
            ),
            ToolDefinition(
                type = "function",
                function = FunctionSchema(
                    name = "trigger_auto_mission",
                    description = "Start a new automated freelancer mission",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "query" to mapOf("type" to "string"),
                            "platforms" to mapOf("type" to "array", "items" to mapOf("type" to "string")),
                            "skills" to mapOf("type" to "array", "items" to mapOf("type" to "string")),
                            "max_proposals" to mapOf("type" to "integer"),
                            "language" to mapOf("type" to "string"),
                            "auto_deliver" to mapOf("type" to "boolean")
                        ),
                        "required" to listOf("query")
                    )
                )
            ),
            ToolDefinition(
                type = "function",
                function = FunctionSchema(
                    name = "export_deliverable",
                    description = "Export a completed job as a deliverable package",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "job_id" to mapOf("type" to "string"),
                            "arabic" to mapOf("type" to "boolean")
                        ),
                        "required" to listOf("job_id")
                    )
                )
            )
        )
    }

    private fun executeAssistantAction(action: AssistantAction) {
        when (action.type) {
            AssistantAction.ActionType.SHOW_JOB -> {
                action.payload["job_id"]?.let { jobId ->
                    // Navigate to job detail
                }
            }
            AssistantAction.ActionType.APPROVE_PROPOSAL -> {
                action.payload["job_id"]?.let { jobId ->
                    viewModelScope.launch { approveProposal(jobId) }
                }
            }
            AssistantAction.ActionType.TRIGGER_AUTOMATION -> {
                // Parse and trigger auto mission
            }
            AssistantAction.ActionType.NAVIGATE -> {
                // Handle navigation
            }
            else -> {}
        }
    }

    fun clearPendingHumanAction() {
        _pendingHumanAction.value = null
        _uiState.value = UIState.Success
    }

    override fun onCleared() {
        repository.disconnect()
        super.onCleared()
    }
}

// UI State sealed class
sealed interface UIState {
    data class Loading : UIState
    data class Success : UIState
    data class Error(val message: String) : UIState
    data class HumanActionRequired(val event: AutomationEvent) : UIState
    data class JobCompleted(val data: AutomationEvent.EventData.JobCompletedData?) : UIState
}