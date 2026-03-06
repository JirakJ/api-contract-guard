package com.jakubjirak.plugin.docs

import com.jakubjirak.plugin.models.ApiContract
import com.jakubjirak.plugin.models.Endpoint
import com.jakubjirak.plugin.models.Schema

/**
 * Generates beautiful API documentation in multiple formats
 */
class DocumentationGenerator {
    
    fun generateMarkdown(contract: ApiContract): String {
        val sb = StringBuilder()
        
        sb.appendLine("# ${contract.title ?: "API"} Documentation")
        sb.appendLine()
        sb.appendLine("**Version:** ${contract.version ?: "1.0.0"}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()
        
        contract.endpoints.forEach { endpoint ->
            sb.appendLine("## ${endpoint.method} ${endpoint.path}")
            sb.appendLine()
            
            if (endpoint.summary != null) {
                sb.appendLine(endpoint.summary)
                sb.appendLine()
            }
            
            if (endpoint.requestBody != null) {
                sb.appendLine("**Request:**")
                sb.appendLine("```json")
                sb.appendLine(generateJsonExample(endpoint.requestBody.schema))
                sb.appendLine("```")
                sb.appendLine()
            }
            
            sb.appendLine("**Response:**")
            endpoint.responses.forEach { response ->
                sb.appendLine("- ${response.statusCode}: ${response.description ?: "Success"}")
            }
            sb.appendLine()
            sb.appendLine("---")
            sb.appendLine()
        }
        
        return sb.toString()
    }
    
    fun generateHtml(contract: ApiContract): String {
        return """
<!DOCTYPE html>
<html>
<head>
    <title>${contract.title ?: "API"} Documentation</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 0 auto; padding: 20px; }
        h1 { color: #2c3e50; }
        .endpoint { background: #f8f9fa; padding: 20px; margin: 20px 0; border-left: 4px solid #3498db; }
        .method { display: inline-block; padding: 5px 10px; color: white; border-radius: 3px; }
        .get { background: #28a745; }
        .post { background: #007bff; }
        pre { background: #2d2d2d; color: #f8f8f2; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>${contract.title ?: "API"} Documentation</h1>
    <p><strong>Version:</strong> ${contract.version ?: "1.0.0"}</p>
    ${contract.endpoints.joinToString("\n") { endpoint -> """
    <div class="endpoint">
        <h3><span class="method ${endpoint.method.toString().lowercase()}">${endpoint.method}</span> ${endpoint.path}</h3>
        <p>${endpoint.summary ?: ""}</p>
    </div>
    """ }}
</body>
</html>
        """.trimIndent()
    }
    
    private fun generateJsonExample(schema: Schema?): String {
        if (schema == null) return "{}"
        
        return when (schema.type.lowercase()) {
            "object" -> {
                val props = schema.properties?.map { (name, _) ->
                    "  \"$name\": \"value\""
                }?.joinToString(",\n") ?: ""
                "{\n$props\n}"
            }
            "array" -> "[]"
            "string" -> "\"example\""
            "integer" -> "123"
            "boolean" -> "true"
            else -> "null"
        }
    }
}
