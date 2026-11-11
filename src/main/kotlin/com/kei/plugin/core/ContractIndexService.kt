package com.kei.plugin.core

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.kei.plugin.models.ApiContract
import com.kei.plugin.models.Endpoint
import com.kei.plugin.settings.KeiSettings
import java.io.IOException

/**
 * Project-level index of the OpenAPI/Swagger contracts found under the project directory.
 */
@Service(Service.Level.PROJECT)
class ContractIndexService(private val project: Project) : Disposable {

    private val index = EndpointIndex()
    private val parser = ContractParser()

    init {
        project.messageBus.connect(this).subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    if (!KeiSettings.getInstance(project).state.autoRefreshContracts) return
                    events.forEach(::onEvent)
                }
            },
        )
    }

    /** Re-scans the whole project directory. Must be called inside a read action or from a background thread. */
    fun indexProject() {
        index.clear()
        val baseDir = project.guessProjectDir() ?: return
        collectContractFiles(baseDir).forEach(::indexFile)
        LOG.info("Indexed ${index.all().size} API contract(s)")
    }

    fun indexFile(file: VirtualFile) {
        val content = try {
            VfsUtilCore.loadText(file)
        } catch (e: IOException) {
            LOG.warn("Cannot read contract ${file.path}", e)
            return
        }
        val contract = parser.parse(file.path, content)
        if (contract == null) index.remove(file.path) else index.put(contract)
    }

    fun findContract(method: String, path: String): Endpoint? = index.find(method, path)

    fun getAllContracts(): List<ApiContract> = index.all()

    fun hasContracts(): Boolean = !index.isEmpty()

    private fun collectContractFiles(dir: VirtualFile): List<VirtualFile> {
        val result = mutableListOf<VirtualFile>()
        VfsUtilCore.iterateChildrenRecursively(
            dir,
            { !(it.isDirectory && ContractFiles.isIgnoredDirectory(it.name)) },
            { file ->
                if (!file.isDirectory && ContractFiles.isCandidate(file.name)) result.add(file)
                true
            },
        )
        return result
    }

    private fun onEvent(event: VFileEvent) {
        if (!isInProject(event.path)) return
        when (event) {
            is VFileDeleteEvent -> index.removeUnder(event.path)
            is VFileContentChangeEvent, is VFileCreateEvent -> {
                val file = event.file ?: return
                if (!file.isDirectory && ContractFiles.isCandidate(file.name)) {
                    // VFS events arrive on the EDT under a write action: parse off the UI thread.
                    ApplicationManager.getApplication().executeOnPooledThread {
                        ReadAction.run<RuntimeException> { if (file.isValid) indexFile(file) }
                    }
                }
            }
            else -> Unit
        }
    }

    private fun isInProject(path: String): Boolean {
        val base = project.basePath ?: return false
        return path == base || path.startsWith("$base/")
    }

    override fun dispose() = index.clear()

    companion object {
        private val LOG = Logger.getInstance(ContractIndexService::class.java)

        fun getInstance(project: Project): ContractIndexService = project.getService(ContractIndexService::class.java)
    }
}
