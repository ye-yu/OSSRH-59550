@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("jvm") version Jetbrains.Kotlin.version
    kotlin("plugin.serialization") version Jetbrains.Kotlin.version
    id("fabric-loom") version Fabric.Loom.version
    id("com.matthewprenger.cursegradle") version CurseGradle.version
    id("org.jetbrains.dokka") version "0.10.1"
    id("maven-publish")
    id("maven")
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

// for publishing to maven local
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
        }

        repositories {
            mavenLocal()
        }
    }
}

tasks.named<Upload>("uploadArchives") {
    repositories {
        withConvention(MavenRepositoryHandlerConvention::class) {
            mavenDeployer {
                beforeDeployment {
                    this.addArtifact(signing.sign(this.pomArtifact).singleSignature)
                }

                withGroovyBuilder {
                    "repository"("url" to "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                        "authentication"("userName" to sonatypeUsername, "password" to sonatypePassword)
                    }

                    "snapshotRepository"("url" to "https://oss.sonatype.org/content/repositories/snapshots/") {
                        "authentication"("userName" to sonatypeUsername, "password" to sonatypePassword)
                    }
                }
                pom.project {
                    withGroovyBuilder {
                        "name"("Just Another MC Gui")
                        "packaging"("jar")
                        // optionally artifactId can be defined here
                        "description"("A Fabric module for widget-based GUI for Minecraft")
                        "url"("https://github.com/ye-yu/OSSRH-59550")

                        "scm" {
                            "connection"("scm:git:ssh://git@github.com:ye-yu/OSSRH-59550.git")
                            "developerConnection"("scm:git:ssh://git@github.com:ye-yu/OSSRH-59550.git")
                            "url"("https://github.com/ye-yu/OSSRH-59550")
                        }

                        "licenses" {
                            "license" {
                                setProperty("name", "MIT")
                                setProperty("url", "https://github.com/ye-yu/OSSRH-59550/blob/master/LICENSE")
                            }
                        }

                        "developers" {
                            "developer" {
                                setProperty("id", "ye-yu")
                                setProperty("name", "Ye Yu")
                                setProperty("email", "rafolwen98@gmail.com")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun DependencyHandlerScope.includeApi(notation: String) {
    include(notation)
    modApi(notation)
}
