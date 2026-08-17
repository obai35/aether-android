package com.aether.companion.data.model

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

@Serializable
data class AIMessage(
    @Json(name = "id") val id: String,
    @Json(name = "role") val role: MessageRole,
    @Json(name = "content") val content: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "tool_calls") val toolCalls: List<ToolCall>?,
    @Json(name = "tool_call_id") val toolCallId: String?,
    @Json(name = "metadata") val metadata: Map<String, Any>?
)

enum class MessageRole(val value: String) {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system"),
    TOOL("tool")
}

@Serializable
data class ToolCall(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "function") val function: FunctionCall
)

@Serializable
data class FunctionCall(
    @Json(name = "name") val name: String,
    @Json(name = "arguments") val arguments: String
)

@Serializable
data class AIAssistantRequest(
    @Json(name = "messages") val messages: List<AIMessage>,
    @Json(name = "tools") val tools: List<ToolDefinition>?,
    @Json(name = "context") val context: AssistantContext?
)

@Serializable
data class ToolDefinition(
    @Json(name = "type") val type: String,
    @Json(name = "function") val function: FunctionSchema
)

@Serializable
data class FunctionSchema(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "parameters") val parameters: Map<String, Any>
)

@Serializable
data class AssistantContext(
    @Json(name = "current_job_id") val currentJobId: String?,
    @Json(name = "automation_state") val automationState: String?,
    @Json(name = "user_preferences") val userPreferences: Map<String, Any>?
)

@Serializable
data class AIAssistantResponse(
    @Json(name = "message") val message: AIMessage,
    @Json(name = "actions") val actions: List<AssistantAction>?
)

@Serializable
data class AssistantAction(
    @Json(name = "type") val type: ActionType,
    @Json(name = "payload") val payload: Map<String, Any>
)

enum class ActionType(val value: String) {
    NAVIGATE("navigate"),
    SHOW_JOB("show_job"),
    APPROVE_PROPOSAL("approve_proposal"),
    TRIGGER_AUTOMATION("trigger_automation"),
    OPEN_SETTINGS("open_settings"),
    EXPORT_DATA("export_data"),
    NONE("none")
}