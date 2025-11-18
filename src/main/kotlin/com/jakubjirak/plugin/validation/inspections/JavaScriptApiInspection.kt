package com.jakubjirak.plugin.validation.inspections

import com.intellij.codeInspection.*
import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.jakubjirak.plugin.core.ContractIndexService
import com.jakubjirak.plugin.models.*
import com.jakubjirak.plugin.quickfixes.AddMissingFieldFix

class JavaScriptApiInspection : LocalInspectionTool() {
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitJSCallExpression(node: JSCallExpression) {
                super.visitJSCallExpression(node)
                
                when {
                    node.isFetchCall() -> validateFetchCall(node, holder)
                    node.isAxiosCall() -> validateAxiosCall(node, holder)
                }
            }
        }
    }
    
    private fun JSCallExpression.isFetchCall(): Boolean {
        return methodExpression?.text == "fetch"
    }
    
    private fun JSCallExpression.isAxiosCall(): Boolean {
        val text = methodExpression?.text ?: return false
        return text.startsWith("axios.") || text.contains(".axios")
    }
    
    private fun validateFetchCall(node: JSCallExpression, holder: ProblemsHolder) {
        val args = node.arguments
        if (args.isEmpty()) return
        
        // Extract endpoint: fetch('/api/users', ...)
        val endpoint = extractStringValue(args[0]) ?: return
        
        // Extract method and body from options
        val options = args.getOrNull(1) as? JSObjectLiteralExpression
        val method = options?.findProperty("method")?.let { 
            extractStringValue(it.value)
        } ?: "GET"
        
        // Find matching contract
        val service = node.project.getService(ContractIndexService::class.java)
        val contract = service.findContract(method, endpoint)
        
        if (contract == null) {
            holder.registerProblem(
                args[0],
                "No contract found for endpoint: $method $endpoint",
                ProblemHighlightType.WARNING
            )
            return
        }
        
        // Validate request body if POST/PUT/PATCH
        if (method in listOf("POST", "PUT", "PATCH") && contract.requestBody != null) {
            val body = options?.findProperty("body")?.value
            if (body != null) {
                validateRequestBody(body, contract.requestBody, holder)
            } else if (contract.requestBody.required) {
                holder.registerProblem(
                    node,
                    "Missing required request body",
                    ProblemHighlightType.ERROR
                )
            }
        }
    }
    
    private fun validateAxiosCall(node: JSCallExpression, holder: ProblemsHolder) {
        val methodExpr = node.methodExpression?.text ?: return
        
        // Extract method: axios.get, axios.post, etc.
        val method = when {
            methodExpr.contains(".get") -> "GET"
            methodExpr.contains(".post") -> "POST"
            methodExpr.contains(".put") -> "PUT"
            methodExpr.contains(".delete") -> "DELETE"
            methodExpr.contains(".patch") -> "PATCH"
            else -> return
        }
        
        val args = node.arguments
        if (args.isEmpty()) return
        
        val endpoint = extractStringValue(args[0]) ?: return
        
        // Find matching contract
        val service = node.project.getService(ContractIndexService::class.java)
        val contract = service.findContract(method, endpoint)
        
        if (contract == null) {
            holder.registerProblem(
                args[0],
                "No contract found for endpoint: $method $endpoint",
                ProblemHighlightType.WARNING
            )
            return
        }
        
        // For axios.post/put/patch, second argument is the body
        if (method in listOf("POST", "PUT", "PATCH") && contract.requestBody != null) {
            val body = args.getOrNull(1)
            if (body != null) {
                validateRequestBody(body, contract.requestBody, holder)
            } else if (contract.requestBody.required) {
                holder.registerProblem(
                    node,
                    "Missing required request body",
                    ProblemHighlightType.ERROR
                )
            }
        }
    }
    
    private fun validateRequestBody(
        body: PsiElement,
        requestBody: RequestBody,
        holder: ProblemsHolder
    ) {
        // Unwrap JSON.stringify if present
        val actualBody = if (body is JSCallExpression && 
                             body.methodExpression?.text == "JSON.stringify") {
            body.arguments.firstOrNull() ?: return
        } else {
            body
        }
        
        when (actualBody) {
            is JSObjectLiteralExpression -> validateObjectLiteral(actualBody, requestBody.schema, holder)
            is JSReferenceExpression -> {
                // Variable reference - we could resolve it, but skip for MVP
            }
        }
    }
    
    private fun validateObjectLiteral(
        obj: JSObjectLiteralExpression,
        schema: Schema,
        holder: ProblemsHolder
    ) {
        val properties = obj.properties.associate { 
            it.name to it.value 
        }
        
        // Check required fields
        schema.required?.forEach { requiredField ->
            if (!properties.containsKey(requiredField)) {
                holder.registerProblem(
                    obj,
                    "Missing required field: '$requiredField'",
                    ProblemHighlightType.ERROR,
                    AddMissingFieldFix(requiredField, schema.properties?.get(requiredField)?.type ?: "string")
                )
            }
        }
        
        // Check field types
        properties.forEach { (fieldName, fieldValue) ->
            val expectedSchema = schema.properties?.get(fieldName)
            if (expectedSchema != null && fieldValue != null) {
                val actualType = inferType(fieldValue)
                if (actualType != null && !isTypeCompatible(actualType, expectedSchema.type)) {
                    holder.registerProblem(
                        fieldValue,
                        "Field '$fieldName' expected type '${expectedSchema.type}', got '$actualType'",
                        ProblemHighlightType.ERROR
                    )
                }
                
                // Validate enum values
                if (expectedSchema.enum != null) {
                    val value = extractStringValue(fieldValue)
                    if (value != null && value !in expectedSchema.enum) {
                        holder.registerProblem(
                            fieldValue,
                            "Invalid enum value. Expected one of: ${expectedSchema.enum.joinToString(", ")}",
                            ProblemHighlightType.ERROR
                        )
                    }
                }
            }
        }
    }
    
    private fun inferType(value: JSExpression): String? {
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
            is JSArrayLiteralExpression -> "array"
            is JSObjectLiteralExpression -> "object"
            else -> null
        }
    }
    
    private fun isTypeCompatible(actualType: String, expectedType: String): Boolean {
        return when {
            actualType == expectedType -> true
            actualType == "integer" && expectedType == "number" -> true
            actualType == "number" && expectedType == "integer" -> false // Stricter check
            else -> false
        }
    }
    
    private fun extractStringValue(element: PsiElement?): String? {
        return when (element) {
            is JSLiteralExpression -> {
                if (element.isQuotedLiteral) {
                    element.stringValue
                } else null
            }
            else -> null
        }
    }
}
