package com.aether.companion.data.model

import com.squareup.moshi.Json

data class AIMessage(
    @Json(name = "id") val id: String,
    @Json(name = "role") val role: MessageRole,
    @Json(name = "content") val content: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "tool_calls") val toolCalls: List<ToolCall>? = null,
    @Json(name = "tool_call_id") val toolCallId: String? = null,
    @Json(name = "metadata") val metadata: Map<String, Any>? = null
) {
    constructor(
        id: String,
        role: MessageRole,
        content: String,
        timestamp: Long
    ) : this(id, role, content, timestamp, null, null, null)
}

enum class MessageRole(val value: String) {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system"),
    TOOL("tool")
}

data class ToolCall(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "function") val function: FunctionCall
)

data class FunctionCall(
    @Json(name = "name") val name: String,
    @Json(name = "arguments") val arguments: String
)

data class AIAssistantRequest(
    @Json(name = "messages") val messages: List<AIMessage>,
    @Json(name = "tools") val tools: List<ToolDefinition>? = null,
    @Json(name = "context") val context: AssistantContext? = null
)

data class ToolDefinition(
    @Json(name = "type") val type: String,
    @Json(name = "function") val function: FunctionSchema
)

data class FunctionSchema(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "parameters") val parameters: Map<String, Any>
)

data class AssistantContext(
    @Json(name = "current_job_id") val currentJobId: String? = null,
    @Json(name = "automation_state") val automationState: String? = null,
    @Json(name = "user_preferences") val userPreferences: Map<String, Any>? = null
)

data class AIAssistantResponse(
    @Json(name = "message") val message: AIMessage,
    @Json(name = "usage") val usage: Usage? = null
) {
    data class Usage(
        @Json(name = "prompt_tokens") val promptTokens: Int,
        @Json(name = "completion_tokens") val completionTokens: Int,
        @Json(name = "total_tokens") val totalTokens: Int
    )
}