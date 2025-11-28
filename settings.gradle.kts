rootProject.name = "kotlin-minesweeper"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
    
    plugins {
        kotlin("jvm") version "1.9.21"
        id("org.jetbrains.compose") version "1.5.11"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// Platform-specific subprojects for cross-platform builds
include(":windows")
include(":linux")
include(":macos")
include(":all")
