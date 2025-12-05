pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "minesweeper"

// Core module with shared code
include(":core")

// Backend modules for each platform
include(":backends:desktop")
include(":backends:android")
include(":backends:web")
