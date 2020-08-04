plugins {
    kotlin("jvm") version Jetbrains.Kotlin.version
    kotlin("plugin.serialization") version Jetbrains.Kotlin.version
    id("fabric-loom") version Fabric.Loom.version
    id("com.matthewprenger.cursegradle") version CurseGradle.version
    id("org.jetbrains.dokka") version "0.10.1"
    id("maven-publish")
    id("signing")
}

group = Info.group
version = Info.version
val sonatypeUsername: String by project
val sonatypePassword: String by project

repositories {
    maven(url = "https://maven.fabricmc.net") { name = "Fabric" }
    maven(url = "https://libraries.minecraft.net/") { name = "Mojang" }
    maven(url = "https://kotlin.bintray.com/kotlinx/") { name = "Kotlinx" }
    mavenCentral()
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
    modImplementation(Mods.modmenu)
    compileOnly("com.google.code.findbugs", "jsr305", "3.0.0")

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
        from(project.tasks["dokka"])
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

// for publishing to maven central
artifacts {
    add("archives", tasks["javadocJar"])
    add("archives", tasks["sourcesJar"])
}

signing {
    sign(configurations.archives.get())
}

java {
    @Suppress("UnstableApiUsage")
    withJavadocJar()
    @Suppress("UnstableApiUsage")
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifacts {
                artifact(tasks["sourcesJar"]) {
                    builtBy(tasks["remapSourcesJar"])
                }

                artifact(tasks["javadocJar"])
                artifact(tasks["remapJar"])
            }

            pom {
                packaging = "jar"
                name.set("Just Another MC Gui")
                description.set("A Fabric module for widget-based GUI for Minecraft\"")
                url.set("https://github.com/ye-yu/OSSRH-59550")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/ye-yu/OSSRH-59550/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        name.set("Ye Yu")
                        id.set("ye-yu")
                        email.set("rafolwen98@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/ye-yu/OSSRH-59550.git")
                    developerConnection.set("scm:git:ssh://git@github.com:ye-yu/OSSRH-59550.git")
                    url.set("https://github.com/ye-yu/OSSRH-59550/")
                }
            }
        }

        repositories {
            maven {
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
            mavenLocal()
        }
    }
}

fun DependencyHandlerScope.includeApi(notation: String) {
    include(notation)
    modApi(notation)
}
