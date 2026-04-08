package com.github.hechtcarmel.jetbrainsindexmcpplugin.actions

import com.github.hechtcarmel.jetbrainsindexmcpplugin.McpConstants
import com.github.hechtcarmel.jetbrainsindexmcpplugin.util.ClientConfigGenerator
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import java.awt.datatransfer.StringSelection

/**
 * Action that shows CLI installation instructions.
 */
class CopyClientConfigAction : AnAction() {

    init {
        templatePresentation.text = "CLI Installation Instructions"
        templatePresentation.description = "Show instructions for installing and using the CLI"
        templatePresentation.icon = AllIcons.FileTypes.Config
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = true
        e.presentation.isEnabled = true
        e.presentation.text = "CLI Installation Instructions"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val instructions = ClientConfigGenerator.generateInstallInstructions()

        CopyPasteManager.getInstance().setContents(StringSelection(instructions))

        showNotification(
            project,
            "CLI Instructions Copied",
            "Installation instructions copied to clipboard.\n\nServer URL: ${ClientConfigGenerator.getServerUrl()}",
            NotificationType.INFORMATION
        )
    }

    private fun showNotification(project: Project?, title: String, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(McpConstants.NOTIFICATION_GROUP_ID)
            .createNotification(title, content, type)
            .notify(project)
    }
}