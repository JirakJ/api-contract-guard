package com.kei.plugin.ui.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import com.kei.plugin.core.ContractIndexService
import com.kei.plugin.ui.ContractExplorerPanel

class RefreshContractsAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Refresh contract index
        val service = project.getService(ContractIndexService::class.java)
        service.indexProject()
        
        // Refresh UI
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Kei Contracts")
        toolWindow?.contentManager?.contents?.forEach { content ->
            val component = content.component
            if (component is ContractExplorerPanel) {
                component.refreshContracts()
            }
        }
    }
}
