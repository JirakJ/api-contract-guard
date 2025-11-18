package com.jakubjirak.plugin.models

import com.intellij.psi.PsiElement

data class ValidationResult(
    val violations: List<Violation> = emptyList(),
    val warnings: List<Violation> = emptyList()
) {
    fun hasErrors() = violations.isNotEmpty()
    fun hasWarnings() = warnings.isNotEmpty()
}

data class Violation(
    val type: ViolationType,
    val message: String,
    val element: PsiElement,
    val field: String? = null,
    val expectedType: String? = null,
    val actualType: String? = null,
    val suggestedFix: String? = null
)

enum class ViolationType {
    MISSING_REQUIRED_FIELD,
    TYPE_MISMATCH,
    INVALID_ENUM_VALUE,
    FORMAT_VIOLATION,
    PATTERN_VIOLATION,
    LENGTH_VIOLATION,
    RANGE_VIOLATION,
    NO_CONTRACT_FOUND,
    DEPRECATED_ENDPOINT,
    UNKNOWN_FIELD
}
