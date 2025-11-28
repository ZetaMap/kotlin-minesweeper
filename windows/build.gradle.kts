import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":"))
    implementation(compose.desktop.windows_x64)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "minesweeper.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "kotlin-minesweeper"
            packageVersion = "1.0.0"
            
            windows {
                menuGroup = "Minesweeper"
                upgradeUuid = "bb981d78-9f73-4578-89a6-4a83197d07e6"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
