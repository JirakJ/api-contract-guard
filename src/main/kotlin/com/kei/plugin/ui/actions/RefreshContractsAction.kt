package com.kei.plugin.ui.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.wm.ToolWindowManager
import com.kei.plugin.core.ContractIndexService
import com.kei.plugin.ui.ContractExplorerPanel

/**
 * Re-scans the project for contracts and refreshes the Kei Contracts tool window.
 */
class RefreshContractsAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ApplicationManager.getApplication().executeOnPooledThread {
            ReadAction.run<RuntimeException> { ContractIndexService.getInstance(project).indexProject() }
            ApplicationManager.getApplication().invokeLater({
                ToolWindowManager.getInstance(project).getToolWindow("Kei Contracts")
                    ?.contentManager?.contents
                    ?.map { it.component }
                    ?.filterIsInstance<ContractExplorerPanel>()
                    ?.forEach { it.refreshContracts() }
            }, project.disposed)
        }
    }
}
