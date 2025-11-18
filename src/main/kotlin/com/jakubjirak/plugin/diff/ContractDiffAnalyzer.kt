package com.jakubjirak.plugin.diff

import com.jakubjirak.plugin.models.*

/**
 * PREMIUM FEATURE: Contract Diff Analyzer
 * Detects breaking and non-breaking changes between contract versions
 */
class ContractDiffAnalyzer {
    
    fun analyzeDiff(oldContract: ApiContract, newContract: ApiContract): DiffResult {
        val breakingChanges = mutableListOf<BreakingChange>()
        val nonBreakingChanges = mutableListOf<Change>()
        
        val oldEndpoints = oldContract.endpoints.associateBy { "${it.method}:${it.path}" }
        val newEndpoints = newContract.endpoints.associateBy { "${it.method}:${it.path}" }
        
        // Check for removed endpoints (BREAKING)
        oldEndpoints.keys.subtract(newEndpoints.keys).forEach { key ->
            breakingChanges.add(BreakingChange(
                type = ChangeType.ENDPOINT_REMOVED,
                description = "Endpoint removed: $key",
                location = key,
                severity = ChangeSeverity.CRITICAL
            ))
        }
        
        // Check for added endpoints (NON-BREAKING)
        newEndpoints.keys.subtract(oldEndpoints.keys).forEach { key ->
            nonBreakingChanges.add(Change(
                type = ChangeType.ENDPOINT_ADDED,
                description = "New endpoint added: $key",
                location = key
            ))
        }
        
        // Compare common endpoints
        oldEndpoints.keys.intersect(newEndpoints.keys).forEach { key ->
            val oldEndpoint = oldEndpoints[key]!!
            val newEndpoint = newEndpoints[key]!!
            
            compareEndpoints(oldEndpoint, newEndpoint, breakingChanges, nonBreakingChanges)
        }
        
        return DiffResult(
            breakingChanges = breakingChanges,
            nonBreakingChanges = nonBreakingChanges,
            hasBreakingChanges = breakingChanges.isNotEmpty()
        )
    }
    
    private fun compareEndpoints(
        old: Endpoint,
        new: Endpoint,
        breaking: MutableList<BreakingChange>,
        nonBreaking: MutableList<Change>
    ) {
        val location = "${old.method}:${old.path}"
        
        // Compare request body
        if (old.requestBody != null && new.requestBody != null) {
            compareSchemas(
                old.requestBody.schema,
                new.requestBody.schema,
                "$location (request)",
                breaking,
                nonBreaking
            )
        }
        
        // Compare responses
        compareResponses(old.responses, new.responses, location, breaking, nonBreaking)
    }
    
    private fun compareSchemas(
        oldSchema: Schema,
        newSchema: Schema,
        location: String,
        breaking: MutableList<BreakingChange>,
        nonBreaking: MutableList<Change>
    ) {
        val oldFields = oldSchema.properties?.keys ?: emptySet()
        val newFields = newSchema.properties?.keys ?: emptySet()
        
        // Check for removed fields (BREAKING)
        oldFields.subtract(newFields).forEach { field ->
            breaking.add(BreakingChange(
                type = ChangeType.FIELD_REMOVED,
                description = "Field removed: $field",
                location = location,
                fieldName = field,
                severity = ChangeSeverity.HIGH
            ))
        }
        
        // Check for added required fields (BREAKING)
        newFields.subtract(oldFields).forEach { field ->
            if (newSchema.required?.contains(field) == true) {
                breaking.add(BreakingChange(
                    type = ChangeType.REQUIRED_FIELD_ADDED,
                    description = "Required field added: $field",
                    location = location,
                    fieldName = field,
                    severity = ChangeSeverity.HIGH
                ))
            } else {
                nonBreaking.add(Change(
                    type = ChangeType.FIELD_ADDED,
                    description = "Optional field added: $field",
                    location = location
                ))
            }
        }
        
        // Check for type changes (BREAKING)
        oldFields.intersect(newFields).forEach { field ->
            val oldType = oldSchema.properties?.get(field)?.type
            val newType = newSchema.properties?.get(field)?.type
            
            if (oldType != newType && oldType != null && newType != null) {
                breaking.add(BreakingChange(
                    type = ChangeType.TYPE_CHANGED,
                    description = "Type changed: $field ($oldType → $newType)",
                    location = location,
                    fieldName = field,
                    oldValue = oldType,
                    newValue = newType,
                    severity = ChangeSeverity.CRITICAL
                ))
            }
        }
    }
    
    private fun compareResponses(
        oldResponses: List<Response>,
        newResponses: List<Response>,
        location: String,
        breaking: MutableList<BreakingChange>,
        nonBreaking: MutableList<Change>
    ) {
        val oldCodes = oldResponses.map { it.statusCode }.toSet()
        val newCodes = newResponses.map { it.statusCode }.toSet()
        
        oldCodes.subtract(newCodes).filter { it in 200..299 }.forEach { code ->
            breaking.add(BreakingChange(
                type = ChangeType.RESPONSE_REMOVED,
                description = "Response removed: $code",
                location = location,
                severity = ChangeSeverity.HIGH
            ))
        }
    }
}

data class DiffResult(
    val breakingChanges: List<BreakingChange>,
    val nonBreakingChanges: List<Change>,
    val hasBreakingChanges: Boolean
)

data class BreakingChange(
    val type: ChangeType,
    val description: String,
    val location: String,
    val severity: ChangeSeverity,
    val fieldName: String? = null,
    val oldValue: String? = null,
    val newValue: String? = null
)

data class Change(
    val type: ChangeType,
    val description: String,
    val location: String
)

enum class ChangeType {
    ENDPOINT_REMOVED,
    FIELD_REMOVED,
    TYPE_CHANGED,
    REQUIRED_FIELD_ADDED,
    FIELD_MADE_REQUIRED,
    RESPONSE_REMOVED,
    ENDPOINT_ADDED,
    FIELD_ADDED,
    FIELD_MADE_OPTIONAL,
    RESPONSE_ADDED
}

enum class ChangeSeverity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}
