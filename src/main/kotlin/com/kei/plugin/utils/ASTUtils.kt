package com.kei.plugin.utils

import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.psi.PsiElement

object ASTUtils {
    
    /**
     * Extract string value from JSExpression
     */
    fun extractStringValue(element: PsiElement?): String? {
        return when (element) {
            is JSLiteralExpression -> {
                if (element.isQuotedLiteral) {
                    element.stringValue
                } else null
            }
            else -> null
        }
    }
    
    /**
     * Infer type from JavaScript expression
     */
    fun inferType(value: JSExpression?): String? {
        return when (value) {
            is JSLiteralExpression -> {
                when {
                    value.isQuotedLiteral -> "string"
                    value.text.toIntOrNull() != null -> "integer"
                    value.text.toDoubleOrNull() != null -> "number"
                    value.text in listOf("true", "false") -> "boolean"
                    value.text == "null" -> "null"
                    else -> null
                }
            }
            is com.intellij.lang.javascript.psi.JSArrayLiteralExpression -> "array"
            is JSObjectLiteralExpression -> "object"
            else -> null
        }
    }
    
    /**
     * Check if two types are compatible
     */
    fun isTypeCompatible(actualType: String, expectedType: String): Boolean {
        return when {
            actualType == expectedType -> true
            actualType == "integer" && expectedType == "number" -> true
            actualType == "number" && expectedType == "integer" -> false
            else -> false
        }
    }
    
    /**
     * Get default value for a type
     */
    fun getDefaultValue(type: String): String {
        return when (type.lowercase()) {
            "string" -> "''"
            "integer", "number" -> "0"
            "boolean" -> "false"
            "array" -> "[]"
            "object" -> "{}"
            else -> "null"
        }
    }
}
