package com.github.aimerny.mcdrdev.action

import com.github.aimerny.mcdrdev.util.invokeLater
import com.github.aimerny.mcdrdev.view.CreatePluginDialog
import com.github.aimerny.mcdrdev.util.runWrite
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

class CreatePluginAction : AnAction() {

    init {
        templatePresentation.icon = IconLoader.getIcon("/icons/mcdr_16.svg", javaClass)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val virtualFile: VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (project != null && virtualFile != null && virtualFile.isDirectory) {
            createPluginDirectory(project, virtualFile)
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        // 确保只有在选中文件夹时才启用 action
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = virtualFile != null && virtualFile.isDirectory
    }

    private fun createPluginDirectory(project: Project, vf: VirtualFile) {
        val dialog = CreatePluginDialog()
        if (dialog.showAndGet()) {
            val pluginId = dialog.getPluginId()
            val pluginName = dialog.getPluginName()
            val author = dialog.getPluginAuthor()
            // create mcdrplugin files
            try {
                // plugin src
                runWrite {
                    writePluginFiles(vf, pluginId, pluginName, author, project)
                }
//                Messages.showMessageDialog(project, "PluginCreated", "Plugin Created", Messages.getInformationIcon())
            } catch (ex: Exception) {
                Messages.showErrorDialog(project, "Failed to create MCDR Plugin: ${ex.message}", "Error")
            }
        }
    }

    private fun writePluginFiles(
        vf: VirtualFile,
        pluginId: String,
        pluginName: String,
        author: String,
        project: Project
    ) {
        val pluginDir = vf.createChildDirectory(this, pluginId)
        val srcDir = pluginDir.createChildDirectory(null, pluginId)
        srcDir.createChildData(null, "__init__.py")
        // plugin meta file
        val pluginMetaJson = pluginDir.createChildData(null, "mcdreforged.plugin.json")
        val content = pluginMetaContent(pluginId, pluginName, author)
        VfsUtil.saveText(pluginMetaJson, content)
        pluginDir.refresh(false, true)
        invokeLater {
            ProjectView.getInstance(project).select(null, pluginDir, false)
        }
    }

    private fun pluginMetaContent(pluginId: String, pluginName: String, author: String): String {
        return """{
                 |  "id": "$pluginId",
                 |  "version": "1.0.0",
                 |  "name": "$pluginName",
                 |  "description": {
                 |    "en_us": "Your Plugin Description",
                 |    "zh_cn": "插件描述"
                 |  },
                 |  "author": "$author",
                 |  "link": "https://github.com/$author/$pluginName",
                 |  "dependencies": {}
                 |}""".trimMargin().format()
    }

}