package io.github.anvell.keepass.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

private const val ExtensionName = "gradleKeePass"

abstract class GradleKeePassPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project
            .extensions
            .create(ExtensionName, GradleKeePassExtension::class.java, project)
    }
}
