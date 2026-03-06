package com.jakubjirak.plugin.performance

import com.jakubjirak.plugin.models.ApiContract
import com.jakubjirak.plugin.models.Endpoint

/**
 * Generates load testing scripts (K6, Gatling, JMeter)
 */
class PerformanceTestGenerator {
    
    fun generateK6Script(contract: ApiContract): String {
        val sb = StringBuilder()
        
        sb.appendLine("// K6 Load Testing Script")
        sb.appendLine("// Generated from: ${contract.file.name}")
        sb.appendLine("import http from 'k6/http';")
        sb.appendLine("import { check, sleep } from 'k6';")
        sb.appendLine()
        sb.appendLine("export const options = {")
        sb.appendLine("  stages: [")
        sb.appendLine("    { duration: '30s', target: 10 },")
        sb.appendLine("    { duration: '1m', target: 50 },")
        sb.appendLine("    { duration: '30s', target: 0 },")
        sb.appendLine("  ],")
        sb.appendLine("  thresholds: {")
        sb.appendLine("    http_req_duration: ['p(95)<500'],")
        sb.appendLine("    http_req_failed: ['rate<0.01'],")
        sb.appendLine("  },")
        sb.appendLine("};")
        sb.appendLine()
        sb.appendLine("const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';")
        sb.appendLine()
        sb.appendLine("export default function() {")
        
        contract.endpoints.take(5).forEach { endpoint ->
            val method = endpoint.method.toString().lowercase()
            sb.appendLine("  let res = http.$method(`\${BASE_URL}${endpoint.path}`);")
            sb.appendLine("  check(res, { 'status is 200': (r) => r.status === 200 });")
        }
        
        sb.appendLine("  sleep(1);")
        sb.appendLine("}")
        
        return sb.toString()
    }
    
    fun generateGatlingScript(contract: ApiContract): String {
        val sb = StringBuilder()
        
        sb.appendLine("// Gatling Load Test")
        sb.appendLine("package simulations")
        sb.appendLine()
        sb.appendLine("import io.gatling.core.Predef._")
        sb.appendLine("import io.gatling.http.Predef._")
        sb.appendLine()
        sb.appendLine("class ApiLoadTest extends Simulation {")
        sb.appendLine("  val httpProtocol = http")
        sb.appendLine("    .baseUrl(\"http://localhost:8080\")")
        sb.appendLine()
        sb.appendLine("  val scn = scenario(\"API Test\")")
        
        contract.endpoints.take(5).forEach { endpoint ->
            val method = endpoint.method.toString().lowercase()
            sb.appendLine("    .exec(http(\"${endpoint.path}\").$method(\"${endpoint.path}\"))")
        }
        
        sb.appendLine()
        sb.appendLine("  setUp(scn.inject(rampUsers(100) during (60)))")
        sb.appendLine("}")
        
        return sb.toString()
    }
}

/**
 */
class PerformanceBenchmarkAnalyzer {
    
    fun analyzeEndpointComplexity(endpoint: Endpoint): ComplexityReport {
        var score = 0
        
        score += (endpoint.requestBody?.schema?.properties?.size ?: 0) * 2
        score += endpoint.responses.sumOf { it.schema?.properties?.size ?: 0 }
        score += (endpoint.parameters?.size ?: 0) * 3
        
        return ComplexityReport(
            endpoint = endpoint,
            complexityScore = score,
            expectedResponseTime = estimateResponseTime(score),
            recommendation = when {
                score < 10 -> "✅ Excellent performance expected"
                score < 30 -> "✅ Good performance"
                score < 50 -> "⚠️ Consider caching"
                else -> "🚨 High complexity - refactor"
            }
        )
    }
    
    private fun estimateResponseTime(score: Int): Int {
        return when {
            score < 10 -> 50
            score < 30 -> 100
            score < 50 -> 200
            else -> 500
        }
    }
}

data class ComplexityReport(
    val endpoint: Endpoint,
    val complexityScore: Int,
    val expectedResponseTime: Int,
    val recommendation: String
)
