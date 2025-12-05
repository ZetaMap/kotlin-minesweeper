import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    sourceSets.jvmMain {
        kotlin.srcDir("src/kotlin")
        resources.srcDir("src/resources")
        dependencies {
            implementation(project(":core"))
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "fr.zetamap.minesweeper.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Minesweeper"
            packageVersion = "1.0.0"

            windows {
                menuGroup = "Minesweeper"
                upgradeUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
            }

            macOS {
                bundleID = "fr.zetamap.minesweeper"
            }

            linux {
                packageName = "minesweeper"
            }
        }
    }
}
