package com.aether.companion.data.model

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
sealed class EventData {
    @Serializable
    data class ProgressData(
        @Json(name = "message") val message: String,
        @Json(name = "stage") val stage: String?,
        @Json(name = "progress_percent") val progressPercent: Int?
    ) : EventData()

    @Serializable
    data class StageData(
        @Json(name = "stage") val stage: String,
        @Json(name = "details") val details: Map<String, Any>?
    ) : EventData()

    @Serializable
    data class QualityGateData(
        @Json(name = "passed") val passed: Boolean,
        @Json(name = "checks") val checks: Map<String, QualityCheck>,
        @Json(name = "blocking_issues") val blockingIssues: List<String>
    ) : EventData()

    @Serializable
    data class HumanRequiredData(
        @Json(name = "reason") val reason: String,
        @Json(name = "action_required") val actionRequired: HumanAction,
        @Json(name = "context") val context: Map<String, Any>?
    ) : EventData()

    @Serializable
    data class JobCompletedData(
        @Json(name = "job") val job: FreelancerJob,
        @Json(name = "deliverable_path") val deliverablePath: String?
    ) : EventData()

    @Serializable
    data class NewJobData(
        @Json(name = "job") val job: FreelancerJob,
        @Json(name = "match_score") val matchScore: Double
    ) : EventData()

    @Serializable
    data class LogData(
        @Json(name = "level") val level: String,
        @Json(name = "message") val message,
        @Json(name = "source") val source: String
    ) : EventData()
}

enum class HumanAction(val value: String) {
    APPROVE_PROPOSAL("approve_proposal"),
    REVIEW_CODE("review_code"),
    PROVIDE_CREDENTIALS("provide_credentials"),
    CONFIRM_DELIVERY("confirm_delivery"),
    RESOLVE_ERROR("resolve_error"),
    CUSTOM("custom");
}