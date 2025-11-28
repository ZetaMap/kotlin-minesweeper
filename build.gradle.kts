import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
    // For Android support, add: id("com.android.application") version "8.2.0"
    // Requires Android SDK to be installed and ANDROID_HOME environment variable set
}

group = "com.minesweeper"
version = "1.0.0"

kotlin {
    // Android target - uncomment when building with Android SDK:
    // androidTarget {
    //     compilations.all {
    //         kotlinOptions {
    //             jvmTarget = "17"
    //         }
    //     }
    // }
    
    jvm("desktop") {
        jvmToolchain(17)
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        
        // Android dependencies - uncomment when building with Android SDK:
        // val androidMain by getting {
        //     dependencies {
        //         implementation("androidx.activity:activity-compose:1.8.1")
        //         implementation("androidx.core:core-ktx:1.12.0")
        //     }
        // }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

// Android configuration - uncomment when building with Android SDK:
// android {
//     namespace = "com.minesweeper"
//     compileSdk = 34
//     
//     defaultConfig {
//         applicationId = "com.minesweeper"
//         minSdk = 24
//         targetSdk = 34
//         versionCode = 1
//         versionName = "1.0.0"
//     }
//     
//     compileOptions {
//         sourceCompatibility = JavaVersion.VERSION_17
//         targetCompatibility = JavaVersion.VERSION_17
//     }
//     
//     buildFeatures {
//         compose = true
//     }
//     
//     composeOptions {
//         kotlinCompilerExtensionVersion = "1.5.4"
//     }
// }

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

// Android build task (requires Android SDK)
tasks.register("android") {
    group = "cross-platform"
    description = "Build Android APK (requires Android SDK - see README for setup)"
    doLast {
        println("To build for Android:")
        println("1. Install Android SDK and set ANDROID_HOME environment variable")
        println("2. Uncomment Android plugin and configuration in build.gradle.kts")
        println("3. Run: ./gradlew assembleDebug")
    }
}
