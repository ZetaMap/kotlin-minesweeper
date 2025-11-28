import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":"))
    implementation(compose.desktop.linux_x64)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "minesweeper.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "kotlin-minesweeper"
            packageVersion = "1.0.0"
            
            linux {
                menuGroup = "Games"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
