package com.kei.plugin.core

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.ToolWindowManager
import com.kei.plugin.ui.ContractExplorerPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Indexes the project's API contracts once the project is opened.
 */
class ContractDiscoveryStartup : ProjectActivity {

    override suspend fun execute(project: Project) {
        val service = ContractIndexService.getInstance(project)
        readAction { service.indexProject() }

        withContext(Dispatchers.EDT) {
            ToolWindowManager.getInstance(project).getToolWindow("Kei Contracts")
                ?.contentManager?.contents
                ?.map { it.component }
                ?.filterIsInstance<ContractExplorerPanel>()
                ?.forEach { it.refreshContracts() }
        }

        val count = service.getAllContracts().size
        if (count > 0) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Kei Notifications")
                .createNotification(
                    "Kei: found $count API contract${if (count > 1) "s" else ""}",
                    "Open the 'Kei Contracts' tool window to explore",
                    NotificationType.INFORMATION,
                )
                .notify(project)
        }
    }
}
