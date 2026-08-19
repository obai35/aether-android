package com.aether.companion.data.model

data class QualityGateResult(
    val lintPassed: Boolean = false,
    val securityPassed: Boolean = false,
    val testsPassed: Boolean = false,
    val details: String = ""
)