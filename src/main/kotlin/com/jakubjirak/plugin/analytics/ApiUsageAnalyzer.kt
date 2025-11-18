package com.jakubjirak.plugin.analytics

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jakubjirak.plugin.models.Endpoint
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * PREMIUM FEATURE: API Usage Analyzer
 */
@Service(Service.Level.PROJECT)
class ApiUsageAnalyzer(private val project: Project) {
    
    private val usageStats = ConcurrentHashMap<String, EndpointUsage>()
    
    companion object {
        fun getInstance(project: Project): ApiUsageAnalyzer = project.service()
    }
    
    fun recordUsage(endpoint: Endpoint, fileName: String) {
        val key = "${endpoint.method}:${endpoint.path}"
        val usage = usageStats.getOrPut(key) {
            EndpointUsage(
                endpoint = endpoint,
                count = 0,
                files = mutableSetOf(),
                firstSeen = Instant.now(),
                lastSeen = Instant.now()
            )
        }
        
        usage.count++
        usage.files.add(fileName)
        usage.lastSeen = Instant.now()
    }
    
    fun getTopEndpoints(limit: Int = 10): List<EndpointUsage> {
        return usageStats.values
            .sortedByDescending { it.count }
            .take(limit)
    }
}

data class EndpointUsage(
    val endpoint: Endpoint,
    var count: Int,
    val files: MutableSet<String>,
    val firstSeen: Instant,
    var lastSeen: Instant
)
