pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        maven("https://plugins.gradle.org/m2/")
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "jamcgui"
