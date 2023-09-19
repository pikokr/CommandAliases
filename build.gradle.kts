plugins {
    idea
    alias(libs.plugins.kotlin)
    alias(libs.plugins.paperweight)
}

group = "moe.paring"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

val kommandVersion: String by project
val kamlVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    paperweight.paperDevBundle(libs.versions.paper)

    implementation("io.github.monun:kommand-api:$kommandVersion")
}

extra.apply {
    set("kotlinVersion", libs.versions.kotlin.get())
    set("kommandVersion", kommandVersion)
    set("paperVersion", libs.versions.paper.get().split('.').take(2).joinToString(separator = "."))
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    create<Copy>("serverJar") {
        from(jar)
        if (File(".server/plugins/${project.name}-$version-dev.jar").exists()) {
            into(".server/plugins/update")
        } else {
            into(".server/plugins")
        }
    }
}

idea {
    module {
        excludeDirs.add(file(".server"))
    }
}
