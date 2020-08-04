pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/kotlin/kotlin-eap") { name = "Kotlin EAP" }
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "jamcgui"

//include(":guitest")