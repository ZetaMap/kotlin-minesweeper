import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser {
            commonWebpackConfig {
                outputFileName = "minesweeper.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "minesweeper.js"
            }
        }
        binaries.executable()
    }

    sourceSets.commonMain {
        kotlin.srcDir("src/kotlin")
        resources.srcDir("src/resources")
        dependencies {
            implementation(project(":core"))
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
        }
    }
}

tasks.register("run") {
    dependsOn("wasmJsBrowserDevelopmentRun")
}

tasks.register("runJs") {
    dependsOn("jsBrowserDevelopmentRun")
}
