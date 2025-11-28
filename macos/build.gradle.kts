import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":"))
    implementation(compose.desktop.macos_x64)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "minesweeper.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "kotlin-minesweeper"
            packageVersion = "1.0.0"
            
            macOS {
                bundleID = "com.minesweeper.app"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
