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

// Only way i found to share computed properties between settings and build
gradle.beforeProject {
  var hasAndroidSdk = System.getenv("ANDROID_HOME") != null
  if( File(settingsDir, "local.properties").exists()) {
    val properties = java.util.Properties()
    properties.load(File(settingsDir, "local.properties").inputStream())
    if (properties.containsKey("sdk.dir")) hasAndroidSdk = true
  }
  if (!hasAndroidSdk) println("No Android SDK found. Skipping Android module.")
  extensions.extraProperties["hasAndroidSdk"] = hasAndroidSdk
}
