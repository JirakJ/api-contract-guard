package com.kei.plugin.history

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.kei.plugin.models.ApiContract
import com.kei.plugin.diff.DiffResult
import com.kei.plugin.diff.ContractDiffAnalyzer
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks contract changes over time
 */
@Service(Service.Level.PROJECT)
class ContractHistoryTracker(private val project: Project) {
    
    private val history = ConcurrentHashMap<VirtualFile, MutableList<ContractVersion>>()
    private val diffAnalyzer = ContractDiffAnalyzer()
    
    companion object {
        fun getInstance(project: Project): ContractHistoryTracker = project.service()
    }
    
    fun recordVersion(file: VirtualFile, contract: ApiContract) {
        val versions = history.getOrPut(file) { mutableListOf() }
        
        val previousVersion = versions.lastOrNull()
        val diff = if (previousVersion != null) {
            diffAnalyzer.analyzeDiff(previousVersion.contract, contract)
        } else null
        
        val newVersion = ContractVersion(
            timestamp = Instant.now(),
            contract = contract,
            version = versions.size + 1,
            diff = diff
        )
        
        versions.add(newVersion)
        
        if (versions.size > 50) {
            versions.removeAt(0)
        }
    }
    
    fun getHistory(file: VirtualFile): List<ContractVersion> {
        return history[file]?.toList() ?: emptyList()
    }
}

data class ContractVersion(
    val timestamp: Instant,
    val contract: ApiContract,
    val version: Int,
    val diff: DiffResult?
)
