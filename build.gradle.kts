plugins {
    `java-library`
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val minecraft = "1.21.4"
val targetJavaVersion = 21

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    group = "io.github.bindglam.weirdcutscene"
    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
    }

    dependencies {
        implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")
        implementation("com.github.retrooper:packetevents-spigot:2.7.0")
        implementation("com.alibaba.fastjson2:fastjson2:2.0.53")
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }

    kotlin {
        jvmToolchain(targetJavaVersion)
    }
}

fun Project.dependency(any: Any) = also { project ->
    if (any is Collection<*>) {
        any.forEach {
            if (it == null) return@forEach
            project.dependencies {
                compileOnly(it)
            }
        }
    } else {
        project.dependencies {
            compileOnly(any)
        }
    }
}

fun Project.paper() = dependency("io.papermc.paper:paper-api:$minecraft-R0.1-SNAPSHOT")

val api = project("api").paper()
val dist = project("dist").paper()
    .dependency(api)

dependencies {
    implementation(api)
    implementation(dist)
}

tasks {
    runServer {
        pluginJars(fileTree("plugins"))
        version(minecraft)
    }

    jar {
        finalizedBy(shadowJar)
    }

    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }

        fun prefix(pattern: String) {
            relocate(pattern, "${project.group}.shaded.$pattern")
        }

        prefix("kotlin")
        prefix("dev.jorel.commandapi")
        prefix("com.github.retrooper.packetevents")
        prefix("io.github.retrooper.packetevents")
        prefix("com.alibaba.fastjson2")
    }
}