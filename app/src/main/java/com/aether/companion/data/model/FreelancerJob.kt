package com.aether.companion.data.model

import com.squareup.moshi.Json

data class FreelancerJob(
    @Json(name = "id") val id: String,
    @Json(name = "offer_id") val offerId: String,
    @Json(name = "title") val title: String,
    @Json(name = "platform") val platform: String,
    @Json(name = "source") val source: String,
    @Json(name = "requirements") val requirements: String,
    @Json(name = "language") val language: String,
    @Json(name = "status") val status: JobStatus,
    @Json(name = "progress") val progress: String,
    @Json(name = "plan") val plan: String?,
    @Json(name = "files") val files: Map<String, String>?,
    @Json(name = "attempts") val attempts: Int,
    @Json(name = "test_result") val testResult: TestResult?,
    @Json(name = "review") val review: CodeReview?,
    @Json(name = "error") val error: String?,
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "updated_at") val updatedAt: Long,
    @Json(name = "quality_gate") val qualityGate: QualityGate?,
    @Json(name = "proposal") val proposal: String?,
    @Json(name = "skill_score") val skillScore: Double?
) {
    enum class JobStatus(val value: String) {
        PENDING("pending"),
        SEARCHING("searching"),
        WORKING("working"),
        AWAITING_APPROVAL("awaiting_approval"),
        IMPLEMENTED("implemented"),
        DELIVERED("delivered"),
        FAILED("failed"),
        NEEDS_FIXES("needs_fixes"),
        QUALITY_GATE_FAILED("quality_gate_failed"),
        REQUIRES_HUMAN("requires_human");

        companion object {
            fun fromString(str: String): JobStatus = values().firstOrNull { it.value == str } ?: PENDING
        }
    }
}

data class TestResult(
    @Json(name = "passed") val passed: Boolean,
    @Json(name = "output") val output: String,
    @Json(name = "tests") val tests: List<TestCase>?
) {
    data class TestCase(
        @Json(name = "name") val name: String,
        @Json(name = "passed") val passed: Boolean,
        @Json(name = "duration") val duration: Long,
        @Json(name = "error") val error: String?
    )
}

data class CodeReview(
    @Json(name = "passed") val passed: Boolean,
    @Json(name = "score") val score: Double,
    @Json(name = "issues") val issues: List<ReviewIssue>,
    @Json(name = "summary") val summary: String
) {
    data class ReviewIssue(
        @Json(name = "file") val file: String,
        @Json(name = "line") val line: Int,
        @Json(name = "severity") val severity: String,
        @Json(name = "message") val message: String,
        @Json(name = "suggestion") val suggestion: String?
    )
}

data class QualityGate(
    @Json(name = "passed") val passed: Boolean,
    @Json(name = "lint_passed") val lintPassed: Boolean,
    @Json(name = "security_passed") val securityPassed: Boolean,
    @Json(name = "tests_passed") val testsPassed: Boolean,
    @Json(name = "issues") val issues: List<QualityIssue>
) {
    data class QualityIssue(
        @Json(name = "type") val type: String,
        @Json(name = "severity") val severity: String,
        @Json(name = "message") val message: String,
        @Json(name = "file") val file: String?,
        @Json(name = "line") val line: Int?
    )
}