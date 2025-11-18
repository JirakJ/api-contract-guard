package com.kei.plugin.core

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContractDiscoveryStartup : StartupActivity {
    
    override fun runActivity(project: Project) {
        // Index contracts in background
        CoroutineScope(Dispatchers.IO).launch {
            val service = project.getService(ContractIndexService::class.java)
            service.indexProject()
            
            val contracts = service.getAllContracts()
            if (contracts.isNotEmpty()) {
                showNotification(project, contracts.size)
            }
        }
    }
    
    private fun showNotification(project: Project, count: Int) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Kei Notifications")
            .createNotification(
                "Kei: Found $count API contract${if (count > 1) "s" else ""}",
                "Open 'Kei Contracts' tool window to explore",
                NotificationType.INFORMATION
            )
            .notify(project)
    }
}
