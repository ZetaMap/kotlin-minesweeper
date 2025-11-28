import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
}

group = "com.minesweeper"
version = "1.0.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "minesweeper.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kotlin-minesweeper"
            packageVersion = "1.0.0"
            
            windows {
                menuGroup = "Minesweeper"
                upgradeUuid = "bb981d78-9f73-4578-89a6-4a83197d07e6"
            }
            
            linux {
                menuGroup = "Games"
            }
            
            macOS {
                bundleID = "com.minesweeper.app"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Unified distribution task - creates distribution for current OS
tasks.register("dist") {
    group = "distribution"
    description = "Create distribution package for the current platform"
    dependsOn("packageDistributionForCurrentOS")
}

// Cross-platform build tasks (packaging requires matching OS)
tasks.register("windows") {
    group = "cross-platform"
    description = "Build Windows distribution (requires Windows OS)"
    finalizedBy("packageMsi")
}

tasks.register("linux") {
    group = "cross-platform"
    description = "Build Linux distribution (requires Linux OS)"
    finalizedBy("packageDeb")
}

tasks.register("macos") {
    group = "cross-platform"
    description = "Build macOS distribution (requires macOS)"
    finalizedBy("packageDmg")
}

tasks.register("all") {
    group = "cross-platform"
    description = "Build all platform distributions (requires respective OS for each format)"
    finalizedBy("packageMsi", "packageDeb", "packageDmg")
}
