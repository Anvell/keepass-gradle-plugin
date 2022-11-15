package io.github.anvell.keepass.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertNotNull
import org.junit.Test

class GradleKeePassPluginTest {

    @Test
    fun `Plugin is applied correctly to the project`() {
        val project = ProjectBuilder
            .builder()
            .build()
        project.pluginManager.apply("io.github.anvell.keepass.gradle.plugin")
    }

    @Test
    fun `Extension gradleKeePass is created correctly`() {
        val project = ProjectBuilder
            .builder()
            .build()
        project.pluginManager.apply("io.github.anvell.keepass.gradle.plugin")

        assertNotNull(project.extensions.getByName("gradleKeePass"))
    }
}
