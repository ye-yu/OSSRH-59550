plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("fabric-loom")
}

group = Info.group
version = Info.version

repositories {
    maven(url = "https://maven.fabricmc.net") { name = "Fabric" }
    maven(url = "https://libraries.minecraft.net/") { name = "Mojang" }
    maven(url = "https://kotlin.bintray.com/kotlinx/") { name = "Kotlinx" }
    mavenCentral()
    mavenLocal()
    jcenter()
}

minecraft {

}

dependencies {
    minecraft("com.mojang", "minecraft", Minecraft.version)
    mappings("net.fabricmc", "yarn", Fabric.YarnMappings.version, classifier = "v2")

    modImplementation("net.fabricmc", "fabric-loader", Fabric.Loader.version)
    modImplementation("net.fabricmc", "fabric-language-kotlin", Fabric.Kotlin.version)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", Fabric.API.version)
    compileOnly("com.google.code.findbugs", "jsr305", "3.0.0")
    implementation(project(":"))

    includeApi(Jetbrains.Kotlin.stdLib)
    includeApi(Jetbrains.Kotlin.reflect)
    includeApi(Jetbrains.Kotlinx.coroutines)
    includeApi(Jetbrains.Kotlinx.serialization)
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")

        from(sourceSets["main"].allSource)

        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    }

    val javadocJar by creating(Jar::class) {
        archiveClassifier.set("javadoc")

        from(project.tasks["javadoc"])

        dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
    }

    compileJava {
        targetCompatibility = "1.8"
        sourceCompatibility = "1.8"
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.ExperimentalStdlibApi"
            )
        }
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                "modid" to Info.modid,
                "name" to Info.name,
                "version" to Info.version,
                "description" to Info.description,
                "kotlinVersion" to Jetbrains.Kotlin.version,
                "fabricApiVersion" to Fabric.API.version
            )
        }
    }
}

fun DependencyHandlerScope.includeApi(notation: String) {
    include(notation)
    modApi(notation)
}