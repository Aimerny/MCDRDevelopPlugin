package com.github.aimerny.mcdrdev.view

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JTextField

class CreatePluginDialog: DialogWrapper(true) {

    private var pluginIdField = JTextField()
    private var pluginNameField = JTextField()
    private var pluginVersionField = JTextField()
    private var pluginAuthorField = JTextField()

    init {
        title = "Create MCDR Plugin"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("ID:") { pluginIdField = textField().component }
            row("Name:") { pluginNameField = textField().component }
            row("Version:") { pluginVersionField = textField().component }
            row("Author:") { pluginAuthorField = textField().component }
        }
    }

    fun getPluginId(): String = pluginIdField.text
    fun getPluginName(): String = pluginNameField.text
    fun getPluginVersion(): String = pluginVersionField.text
    fun getPluginAuthor(): String = pluginAuthorField.text
}