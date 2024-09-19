package com.github.aimerny.mcdrdev.action

import com.github.aimerny.mcdrdev.view.CreatePluginDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

class CreatePluginAction : AnAction() {

    init {
        templatePresentation.icon = IconLoader.getIcon("/icons/mcdr.svg", javaClass)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val virtualFile: VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (project != null && virtualFile != null && virtualFile.isDirectory) {
            createPluginDirectory(project, virtualFile)
        }
    }

    override fun update(e: AnActionEvent) {
        // 确保只有在选中文件夹时才启用 action
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = virtualFile != null && virtualFile.isDirectory
    }

    private fun createPluginDirectory(project: Project, vf: VirtualFile) {
        val dialog = CreatePluginDialog()
        if (dialog.showAndGet()){
            val pluginId = dialog.getPluginId()
            val pluginName = dialog.getPluginName()
            val pluginVersion = dialog.getPluginVersion()
            val author = dialog.getPluginAuthor()

            // create mcdrplugin files
            try {
                // plugin src
                WriteAction.run<Throwable> {
                    val pluginDir = vf.createChildDirectory(this, pluginId)
                    pluginDir.createChildData(null, "__init__.py")
                    // plugin meta file
                    val pluginMetaJson = vf.createChildData(this, "mcdreforged.plugin.json")
                    val content = """{
                |  "id": "$pluginId",
                |  "version": "$pluginVersion",
                |  "name": "$pluginName",
                |  "description": {
                |    "en_us": "Your Plugin Description",
                |    "zh_cn": "你的插件描述"
                |  },
                |  "author": "$author",
                |  "link": "Your plugin link",
                |  "dependencies": {}
                |}
                """.trimMargin().format()
                    VfsUtil.saveText(pluginMetaJson, content)
                }
                Messages.showMessageDialog(project, "PluginCreated", "Plugin Created", Messages.getInformationIcon())
            }catch (ex: Exception){
                Messages.showErrorDialog(project, "Failed to create MCDR Plugin: ${ex.message}", "Error")
            }

        }else {
            Messages.showErrorDialog(project, "Please select a directory", "Error")
        }
    }

}