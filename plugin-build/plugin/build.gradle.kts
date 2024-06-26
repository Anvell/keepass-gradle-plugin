@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.pluginPublish)
}

kotlin { jvmToolchain(11) }

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = property("VERSION").toString()
            displayName = property("DISPLAY_NAME").toString()
        }
    }
}

pluginBundle {
    website = property("WEBSITE").toString()
    vcsUrl = property("VCS_URL").toString()
    description = property("DESCRIPTION").toString()
    tags = listOf(
        "keepass",
        "secrets",
        "properties",
        "credentials"
    )
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.kotpass)
    implementation(libs.okio)

    testImplementation(libs.junit)
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
