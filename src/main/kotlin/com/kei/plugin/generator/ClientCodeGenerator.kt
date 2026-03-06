package com.kei.plugin.generator

import com.kei.plugin.models.*

/**
 * Generates typed API clients from OpenAPI contracts
 */
class ClientCodeGenerator {
    
    fun generateTypeScriptClient(contract: ApiContract): String {
        val sb = StringBuilder()
        
        sb.appendLine("// Auto-generated TypeScript API Client")
        sb.appendLine("// Generated from: ${contract.file.name}")
        sb.appendLine()
        sb.appendLine("export class ${contract.title?.toPascalCase() ?: "Api"}Client {")
        sb.appendLine("  private baseUrl: string;")
        sb.appendLine()
        sb.appendLine("  constructor(baseUrl: string) {")
        sb.appendLine("    this.baseUrl = baseUrl;")
        sb.appendLine("  }")
        sb.appendLine()
        
        contract.endpoints.forEach { endpoint ->
            sb.appendLine(generateTypeScriptMethod(endpoint))
        }
        
        sb.appendLine("}")
        
        return sb.toString()
    }
    
    private fun generateTypeScriptMethod(endpoint: Endpoint): String {
        val sb = StringBuilder()
        val methodName = endpoint.operationId ?: "request"
        
        sb.appendLine("  async $methodName(): Promise<any> {")
        sb.appendLine("    const url = `\${this.baseUrl}${endpoint.path}`;")
        sb.appendLine("    const response = await fetch(url, {")
        sb.appendLine("      method: '${endpoint.method}'")
        sb.appendLine("    });")
        sb.appendLine("    return response.json();")
        sb.appendLine("  }")
        sb.appendLine()
        
        return sb.toString()
    }
    
    fun generatePythonClient(contract: ApiContract): String {
        val sb = StringBuilder()
        
        sb.appendLine("# Auto-generated Python API Client")
        sb.appendLine("import requests")
        sb.appendLine()
        sb.appendLine("class ${contract.title?.toPascalCase() ?: "Api"}Client:")
        sb.appendLine("    def __init__(self, base_url: str):")
        sb.appendLine("        self.base_url = base_url")
        sb.appendLine()
        
        contract.endpoints.forEach { endpoint ->
            sb.appendLine(generatePythonMethod(endpoint))
        }
        
        return sb.toString()
    }
    
    private fun generatePythonMethod(endpoint: Endpoint): String {
        val sb = StringBuilder()
        val methodName = endpoint.operationId ?: "request"
        val method = endpoint.method.toString().lowercase()
        
        sb.appendLine("    def $methodName(self):")
        sb.appendLine("        url = f\"{self.baseUrl}${endpoint.path}\"")
        sb.appendLine("        return requests.$method(url).json()")
        sb.appendLine()
        
        return sb.toString()
    }
}

private fun String.toPascalCase(): String {
    return this.split(Regex("[^a-zA-Z0-9]+"))
        .filter { it.isNotEmpty() }
        .joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } }
}
