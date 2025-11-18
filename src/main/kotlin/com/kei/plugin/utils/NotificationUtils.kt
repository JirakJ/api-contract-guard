package com.kei.plugin.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object NotificationUtils {
    
    private const val NOTIFICATION_GROUP = "Kei Notifications"
    
    fun showInfo(project: Project, title: String, content: String) {
        show(project, title, content, NotificationType.INFORMATION)
    }
    
    fun showWarning(project: Project, title: String, content: String) {
        show(project, title, content, NotificationType.WARNING)
    }
    
    fun showError(project: Project, title: String, content: String) {
        show(project, title, content, NotificationType.ERROR)
    }
    
    fun showContractDiscovered(project: Project, count: Int) {
        val plural = if (count > 1) "s" else ""
        showInfo(
            project,
            "Kei: Found $count API contract$plural",
            "Open 'Kei Contracts' tool window to explore"
        )
    }
    
    fun showContractError(project: Project, fileName: String, error: String) {
        showError(
            project,
            "Kei: Failed to parse contract",
            "Error in $fileName: $error"
        )
    }
    
    fun showBreakingChange(project: Project, contractName: String, changeCount: Int) {
        showWarning(
            project,
            "Kei: Breaking changes detected",
            "$changeCount breaking change(s) in $contractName"
        )
    }
    
    private fun show(project: Project, title: String, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP)
            .createNotification(title, content, type)
            .notify(project)
    }
}
