package com.github.aimerny.mcdrdev.util

import java.io.File

fun getPluginMetaContent(args: Map<String, String>): String {
    val file = File("templates/pluginTemplate", "/mcdreforged.plugin.json.template")
    return file.path
}
