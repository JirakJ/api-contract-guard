package com.kei.plugin.mockdata

import com.kei.plugin.models.Schema
import kotlin.random.Random

/**
 * PREMIUM FEATURE: Mock Data Generator
 * Generates realistic mock data based on JSON Schema
 */
class MockDataGenerator {
    
    fun generateMockData(schema: Schema): Any? {
        return when (schema.type.lowercase()) {
            "string" -> generateStringValue(schema)
            "integer" -> Random.nextInt(0, 1000)
            "number" -> Random.nextDouble(0.0, 1000.0)
            "boolean" -> Random.nextBoolean()
            "array" -> generateArrayValue(schema)
            "object" -> generateObjectValue(schema)
            else -> null
        }
    }
    
    private fun generateStringValue(schema: Schema): String {
        return when (schema.format?.lowercase()) {
            "email" -> "user${Random.nextInt(1000)}@example.com"
            "uuid" -> generateUuid()
            "date" -> "2024-${Random.nextInt(1, 13).toString().padStart(2, '0')}-15"
            "date-time" -> "2024-01-15T10:30:00Z"
            else -> {
                if (!schema.enum.isNullOrEmpty()) {
                    schema.enum.random()
                } else {
                    "sample_${Random.nextInt(100)}"
                }
            }
        }
    }
    
    private fun generateArrayValue(schema: Schema): List<Any?> {
        val itemSchema = schema.items ?: Schema(type = "string")
        val count = Random.nextInt(1, 5)
        return (1..count).map { generateMockData(itemSchema) }
    }
    
    private fun generateObjectValue(schema: Schema): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        
        schema.properties?.forEach { (name, propSchema) ->
            val isRequired = schema.required?.contains(name) == true
            if (isRequired || Random.nextBoolean()) {
                result[name] = generateMockData(propSchema)
            }
        }
        
        return result
    }
    
    private fun generateUuid(): String {
        return "${randomHex(8)}-${randomHex(4)}-${randomHex(4)}-${randomHex(4)}-${randomHex(12)}"
    }
    
    private fun randomHex(length: Int): String {
        val chars = "0123456789abcdef"
        return (1..length).map { chars.random() }.joinToString("")
    }
}
