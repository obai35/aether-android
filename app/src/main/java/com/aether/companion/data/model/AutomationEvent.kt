package com.aether.companion.data.model

import com.squareup.moshi.Json

data class AutomationEvent(
    @Json(name = "type") val type: EventType,
    @Json(name = "data") val data: EventData,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "job_id") val jobId: String?
)

enum class EventType(val value: String) {
    JOB_STARTED("job_started"),
    JOB_PROGRESS("job_progress"),
    STAGE_CHANGED("stage_changed"),
    QUALITY_GATE_STARTED("quality_gate_started"),
    QUALITY_GATE_COMPLETED("quality_gate_completed"),
    HUMAN_REQUIRED("human_required"),
    JOB_COMPLETED("job_completed"),
    JOB_FAILED("job_failed"),
    NEW_JOB_FOUND("new_job_found"),
    PROPOSAL_GENERATED("proposal_generated"),
    DELIVERABLE_READY("deliverable_ready"),
    HEARTBEAT("heartbeat"),
    LOG("log");

    companion object {
        fun fromString(str: String): EventType = values().firstOrNull { it.value == str } ?: LOG
    }
}

sealed interface EventData {
    data class ProgressData(
        @Json(name = "message") val message: String,
        @Json(name = "stage") val stage: String?,
        @Json(name = "progress_percent") val progressPercent: Int?
    ) : EventData

    data class StageData(
        @Json(name = "stage") val stage: String,
        @Json(name = "details") val details: Map<String, Any>?
    ) : EventData

    data class QualityGateData(
        @Json(name = "passed") val passed: Boolean,
        @Json(name = "checks") val checks: Map<String, QualityCheck>,
        @Json(name = "blocking_issues") val blockingIssues: List<String>
    ) : EventData

    data class HumanRequiredData(
        @Json(name = "reason") val reason: String,
        @Json(name = "details") val details: Map<String, Any>?
    ) : EventData

    data class JobCompletedData(
        @Json(name = "deliverable_url") val deliverableUrl: String?,
        @Json(name = "summary") val summary: String
    ) : EventData

    data class LogData(
        @Json(name = "level") val level: String,
        @Json(name = "message") val message: String
    ) : EventData
}

data class QualityCheck(
    @Json(name = "name") val name: String,
    @Json(name = "passed") val passed: Boolean,
    @Json(name = "details") val details: String?
)