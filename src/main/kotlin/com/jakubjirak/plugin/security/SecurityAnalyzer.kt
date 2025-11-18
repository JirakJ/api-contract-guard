package com.jakubjirak.plugin.security

import com.jakubjirak.plugin.models.ApiContract
import com.jakubjirak.plugin.models.Endpoint
import com.jakubjirak.plugin.models.HttpMethod

/**
 * PREMIUM FEATURE: Security Analyzer
 * Analyzes API contracts for security vulnerabilities and best practices
 */
class SecurityAnalyzer {
    
    fun analyzeContract(contract: ApiContract): SecurityReport {
        val vulnerabilities = mutableListOf<SecurityVulnerability>()
        
        contract.endpoints.forEach { endpoint ->
            vulnerabilities.addAll(analyzeEndpoint(endpoint))
        }
        
        val score = calculateSecurityScore(vulnerabilities)
        val recommendations = generateRecommendations(vulnerabilities)
        
        return SecurityReport(
            score = score,
            level = determineSecurityLevel(score),
            vulnerabilities = vulnerabilities,
            recommendations = recommendations,
            totalEndpoints = contract.endpoints.size,
            secureEndpoints = contract.endpoints.size - vulnerabilities.map { it.endpoint }.distinct().size
        )
    }
    
    private fun analyzeEndpoint(endpoint: Endpoint): List<SecurityVulnerability> {
        val vulnerabilities = mutableListOf<SecurityVulnerability>()
        
        // Check for missing authentication
        if (!hasAuthentication(endpoint)) {
            vulnerabilities.add(SecurityVulnerability(
                type = VulnerabilityType.MISSING_AUTHENTICATION,
                severity = if (endpoint.method in listOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)) 
                    Severity.HIGH else Severity.MEDIUM,
                endpoint = endpoint,
                description = "Endpoint lacks authentication requirements",
                recommendation = "Add security scheme (OAuth2, JWT, API Key)"
            ))
        }
        
        // Check for sensitive data exposure
        if (containsSensitiveFields(endpoint)) {
            vulnerabilities.add(SecurityVulnerability(
                type = VulnerabilityType.SENSITIVE_DATA_EXPOSURE,
                severity = Severity.HIGH,
                endpoint = endpoint,
                description = "Endpoint may expose sensitive data (password, token, secret)",
                recommendation = "Exclude sensitive fields from response or use proper masking"
            ))
        }
        
        // Check for missing rate limiting
        if (endpoint.method in listOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)) {
            vulnerabilities.add(SecurityVulnerability(
                type = VulnerabilityType.MISSING_RATE_LIMIT,
                severity = Severity.MEDIUM,
                endpoint = endpoint,
                description = "No rate limiting defined for mutation endpoint",
                recommendation = "Implement rate limiting to prevent abuse"
            ))
        }
        
        // Check for SQL injection risks
        if (hasSqlInjectionRisk(endpoint)) {
            vulnerabilities.add(SecurityVulnerability(
                type = VulnerabilityType.SQL_INJECTION_RISK,
                severity = Severity.CRITICAL,
                endpoint = endpoint,
                description = "Path parameter may be vulnerable to SQL injection",
                recommendation = "Use parameterized queries and input validation"
            ))
        }
        
        // Check for XSS risks
        if (hasXssRisk(endpoint)) {
            vulnerabilities.add(SecurityVulnerability(
                type = VulnerabilityType.XSS_RISK,
                severity = Severity.HIGH,
                endpoint = endpoint,
                description = "String parameters without validation may enable XSS attacks",
                recommendation = "Implement input sanitization and output encoding"
            ))
        }
        
        // Check for insecure data transmission
        if (!endpoint.path.startsWith("/api/public") && !hasHttpsOnly(endpoint)) {
            vulnerabilities.add(SecurityVulnerability(
                type = VulnerabilityType.INSECURE_TRANSMISSION,
                severity = Severity.MEDIUM,
                endpoint = endpoint,
                description = "Endpoint not explicitly marked as HTTPS-only",
                recommendation = "Enforce HTTPS for all sensitive endpoints"
            ))
        }
        
        return vulnerabilities
    }
    
    private fun hasAuthentication(endpoint: Endpoint): Boolean {
        return endpoint.description?.contains("auth", ignoreCase = true) == true ||
               endpoint.summary?.contains("authenticated", ignoreCase = true) == true
    }
    
    private fun containsSensitiveFields(endpoint: Endpoint): Boolean {
        val sensitiveKeywords = listOf("password", "secret", "token", "key", "credential", "ssn", "credit")
        
        val requestFields = endpoint.requestBody?.schema?.properties?.keys ?: emptySet()
        val responseFields = endpoint.responses.flatMap { 
            it.schema?.properties?.keys ?: emptySet() 
        }
        
        return (requestFields + responseFields).any { field ->
            sensitiveKeywords.any { keyword -> 
                field.contains(keyword, ignoreCase = true)
            }
        }
    }
    
    private fun hasSqlInjectionRisk(endpoint: Endpoint): Boolean {
        return endpoint.parameters?.any { param ->
            param.schema.type == "string" && 
            param.location.toString().contains("PATH") &&
            param.schema.pattern == null
        } == true
    }
    
    private fun hasXssRisk(endpoint: Endpoint): Boolean {
        return endpoint.requestBody?.schema?.properties?.any { (_, schema) ->
            schema.type == "string" && 
            schema.format == null &&
            schema.pattern == null
        } == true
    }
    
    private fun hasHttpsOnly(endpoint: Endpoint): Boolean {
        return endpoint.description?.contains("https", ignoreCase = true) == true ||
               endpoint.summary?.contains("secure", ignoreCase = true) == true
    }
    
    private fun calculateSecurityScore(vulnerabilities: List<SecurityVulnerability>): Int {
        var score = 100
        
        vulnerabilities.forEach { vuln ->
            score -= when (vuln.severity) {
                Severity.CRITICAL -> 20
                Severity.HIGH -> 15
                Severity.MEDIUM -> 10
                Severity.LOW -> 5
            }
        }
        
        return maxOf(0, score)
    }
    
    private fun determineSecurityLevel(score: Int): SecurityLevel {
        return when {
            score >= 90 -> SecurityLevel.EXCELLENT
            score >= 75 -> SecurityLevel.GOOD
            score >= 60 -> SecurityLevel.FAIR
            score >= 40 -> SecurityLevel.POOR
            else -> SecurityLevel.CRITICAL
        }
    }
    
    private fun generateRecommendations(vulnerabilities: List<SecurityVulnerability>): List<String> {
        val recommendations = mutableSetOf<String>()
        
        val criticalCount = vulnerabilities.count { it.severity == Severity.CRITICAL }
        val highCount = vulnerabilities.count { it.severity == Severity.HIGH }
        
        if (criticalCount > 0) {
            recommendations.add("🚨 URGENT: Fix $criticalCount critical security vulnerabilities immediately")
        }
        
        if (highCount > 0) {
            recommendations.add("⚠️ Address $highCount high-severity security issues")
        }
        
        if (vulnerabilities.any { it.type == VulnerabilityType.MISSING_AUTHENTICATION }) {
            recommendations.add("Implement authentication for all non-public endpoints")
        }
        
        if (vulnerabilities.any { it.type == VulnerabilityType.SENSITIVE_DATA_EXPOSURE }) {
            recommendations.add("Review and mask sensitive data in API responses")
        }
        
        if (vulnerabilities.any { it.type == VulnerabilityType.SQL_INJECTION_RISK }) {
            recommendations.add("Add input validation and use parameterized queries")
        }
        
        recommendations.add("Consider implementing API security best practices (OWASP API Security Top 10)")
        
        return recommendations.toList()
    }
}

data class SecurityReport(
    val score: Int,
    val level: SecurityLevel,
    val vulnerabilities: List<SecurityVulnerability>,
    val recommendations: List<String>,
    val totalEndpoints: Int,
    val secureEndpoints: Int
) {
    val vulnerableEndpoints: Int
        get() = totalEndpoints - secureEndpoints
        
    val criticalVulnerabilities: Int
        get() = vulnerabilities.count { it.severity == Severity.CRITICAL }
        
    val highVulnerabilities: Int
        get() = vulnerabilities.count { it.severity == Severity.HIGH }
}

data class SecurityVulnerability(
    val type: VulnerabilityType,
    val severity: Severity,
    val endpoint: Endpoint,
    val description: String,
    val recommendation: String
)

enum class VulnerabilityType {
    MISSING_AUTHENTICATION,
    SENSITIVE_DATA_EXPOSURE,
    MISSING_RATE_LIMIT,
    SQL_INJECTION_RISK,
    XSS_RISK,
    INSECURE_TRANSMISSION,
    WEAK_ENCRYPTION,
    INSUFFICIENT_LOGGING
}

enum class Severity {
    CRITICAL, HIGH, MEDIUM, LOW
}

enum class SecurityLevel {
    EXCELLENT, GOOD, FAIR, POOR, CRITICAL
}
