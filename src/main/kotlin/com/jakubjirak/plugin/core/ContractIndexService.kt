package com.jakubjirak.plugin.core

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.jakubjirak.plugin.models.ApiContract
import com.jakubjirak.plugin.models.Endpoint
import com.jakubjirak.plugin.models.HttpMethod
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
class ContractIndexService(private val project: Project) {
    
    private val contractCache = ConcurrentHashMap<VirtualFile, ApiContract>()
    private val endpointIndex = ConcurrentHashMap<String, MutableList<Endpoint>>()
    private val parser = ContractParser()
    
    init {
        // Listen for file changes
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    events.forEach { event ->
                        when (event) {
                            is VFileContentChangeEvent -> onFileChanged(event.file)
                            is VFileCreateEvent -> event.file?.let { onFileCreated(it) }
                            is VFileDeleteEvent -> onFileDeleted(event.file)
                        }
                    }
                }
            }
        )
    }
    
    fun indexProject() {
        contractCache.clear()
        endpointIndex.clear()
        
        // Find all contract files in project
        val contractFiles = findContractFiles()
        contractFiles.forEach { file ->
            indexFile(file)
        }
    }
    
    fun indexFile(file: VirtualFile) {
        val contract = parser.parse(file) ?: return
        contractCache[file] = contract
        
        // Build endpoint index for fast lookup
        contract.endpoints.forEach { endpoint ->
            val key = buildEndpointKey(endpoint.method, endpoint.path)
            endpointIndex.getOrPut(key) { mutableListOf() }.add(endpoint)
        }
    }
    
    fun findContract(method: String, path: String): Endpoint? {
        val httpMethod = try {
            HttpMethod.valueOf(method.uppercase())
        } catch (e: Exception) {
            return null
        }
        
        val key = buildEndpointKey(httpMethod, path)
        val endpoints = endpointIndex[key]
        return endpoints?.firstOrNull()
    }
    
    fun getAllContracts(): List<ApiContract> {
        return contractCache.values.toList()
    }
    
    fun getContract(file: VirtualFile): ApiContract? {
        return contractCache[file]
    }
    
    private fun findContractFiles(): List<VirtualFile> {
        val files = mutableListOf<VirtualFile>()
        val baseDir = project.guessProjectDir() ?: return files
        
        scanDirectory(baseDir, files)
        return files
    }
    
    private fun scanDirectory(dir: VirtualFile, results: MutableList<VirtualFile>) {
        if (!dir.isDirectory) return
        
        // Skip common directories
        if (dir.name in setOf("node_modules", ".git", "build", "dist", "target", ".idea")) {
            return
        }
        
        dir.children.forEach { file ->
            if (file.isDirectory) {
                scanDirectory(file, results)
            } else if (isContractFile(file)) {
                results.add(file)
            }
        }
    }
    
    private fun isContractFile(file: VirtualFile): Boolean {
        val name = file.name.lowercase()
        return (name.contains("openapi") || name.contains("swagger") || name.contains("api")) &&
               (name.endsWith(".yaml") || name.endsWith(".yml") || name.endsWith(".json"))
    }
    
    private fun buildEndpointKey(method: HttpMethod, path: String): String {
        // Normalize path: /api/users/{id} -> /api/users/*
        val normalizedPath = path.replace(Regex("\\{[^}]+\\}"), "*")
        return "${method.name}:$normalizedPath"
    }
    
    private fun onFileChanged(file: VirtualFile) {
        if (isContractFile(file)) {
            // Re-index changed contract file
            contractCache.remove(file)
            indexFile(file)
        }
    }
    
    private fun onFileCreated(file: VirtualFile) {
        if (isContractFile(file)) {
            indexFile(file)
        }
    }
    
    private fun onFileDeleted(file: VirtualFile) {
        contractCache.remove(file)
        // Remove endpoints from index
        // TODO: Clean up endpoint index
    }
}
