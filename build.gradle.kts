import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "fr.zetamap.minesweeper"
version = "1.0.0"
val appName = "Minesweeper"
val hasAndroidSdk: Boolean by project

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.composeHotReload)
}

kotlin {
  // This will display a warning if no Android SDK is found, but we don't care
  if (hasAndroidSdk) {
    androidTarget {
      compilerOptions.jvmTarget = JvmTarget.JVM_17
    }
  }

  jvm()

  js {
    browser {
      commonWebpackConfig {
        outputFileName = "${rootProject.name}.js"
      }
    }
    binaries.executable()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "${rootProject.name}.js"
      }
    }
    binaries.executable()
  }

  sourceSets {
    if (hasAndroidSdk) {
      androidMain {
        kotlin.srcDir("android/src")
        resources.srcDir("android/res")
        dependencies {
          implementation(compose.preview)
          implementation(libs.androidx.activity.compose)
        }
      }
    }
    commonMain {
      kotlin.srcDir("core/src")
      resources.srcDir("core/resources"/*"core/assets"*/)
      dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(compose.components.uiToolingPreview)
        implementation(libs.androidx.lifecycle.viewmodelCompose)
        implementation(libs.androidx.lifecycle.runtimeCompose)
      }
    }
    jvmMain {
      kotlin.srcDir("desktop/src")
      resources.srcDir("desktop/resources")
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutinesSwing)
      }
    }
    webMain {
      kotlin.srcDir("web/src")
      resources.srcDir("web/resources")
    }
  }
}

android {
  namespace = project.group.toString()
  buildToolsVersion = "36.0.0"
  compileSdk = 36

  sourceSets["main"].apply {
    manifest.srcFile("android/AndroidManifest.xml")
    kotlin.srcDir("android/src")
    res.srcDir("android/res")
  }

  defaultConfig {
    applicationId = project.group.toString()
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = project.version.toString()
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildTypes.all {
    isMinifyEnabled = true
    //proguardFiles("proguard-rules.pro")
  }
}

dependencies {
  debugImplementation(compose.uiTooling)
}

compose.desktop {
  application {
    mainClass = "${rootProject.group}.desktop.MainKt"
    val icons = kotlin.sourceSets["commonMain"].resources.srcDirs
                      .map { it.resolve("assets/icons") }.first { it.exists() }

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = appName
      packageVersion = rootProject.version.toString()

      windows {
        menuGroup = appName
        iconFile = icons.resolve("icon.ico")
        upgradeUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
      }

      macOS {
        bundleID = rootProject.group.toString()
        iconFile = icons.resolve("icon.icns")
      }

      linux {
        packageName = rootProject.name
        appCategory = "game"
        iconFile = icons.resolve("icon.png")
      }
    }

    buildTypes.release.proguard {

      isEnabled = true
      optimize = true
      //configurationFiles.from(kotlin.sourceSets["jvmMain"].file("proguard-rules.pro"))
    }
  }
}

// Aliases
//TODO: copy builded package/apk to another folder and zip the builded js
if (hasAndroidSdk) {
  tasks.register("androidDistDebug") { dependsOn("assembleDebug") }
  tasks.register("androidDistRelease") { dependsOn("assembleRelease") }
  tasks.register("androidDistBundle") { dependsOn("bundleRelease") }
  tasks.register("androidDist") { dependsOn("androidDistRelease") }
  tasks.register("androidInstallDebug") { dependsOn("installDebug") }
  tasks.register("androidInstall") { dependsOn("installRelease") }
  tasks.register("androidUninstallDebug") { dependsOn("uninstallDebug") }
  tasks.register("androidUninstall") { dependsOn("uninstallRelease") }
  tasks.register<Exec>("androidRun") {
    // Manually start the activity on the phone
    commandLine(android.adbExecutable.absolutePath, "shell", "am", "start", "-n",
                "${project.group}/${project.group}.android.MainActivity")
  }
}

tasks.register("desktopDistWindows") { dependsOn("packageReleaseMsi") }
tasks.register("desktopDistMacos") { dependsOn("packageReleaseDmg") }
tasks.register("desktopDistLinux") { dependsOn("packageReleaseDeb") }
tasks.register("desktopDist") { dependsOn("packageReleaseDistributionForCurrentOS") }
tasks.register("desktopJar") { dependsOn("packageReleaseUberJarForCurrentOS") }
tasks.register("desktopRunDebug") { dependsOn("hotRunJvm") }
tasks.register("desktopRun") { dependsOn("run") }

tasks.register("webDistWasm") { dependsOn("wasmJsBrowserDistribution") }
tasks.register("webDistJs") { dependsOn("jsBrowserDistribution") }
tasks.register("webDist") { dependsOn("webDistJs") }
tasks.register("webRunWasm") { dependsOn("wasmJsBrowserDevelopmentRun") }
tasks.register("webRunJs") { dependsOn("jsBrowserDevelopmentRun") }
tasks.register("webRun") { dependsOn("webRunJs") }
