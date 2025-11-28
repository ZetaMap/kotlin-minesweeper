// Aggregator project that builds all platform targets

plugins {
    base
}

tasks.register("buildAll") {
    description = "Build for all platforms"
    group = "build"
    dependsOn(":windows:build", ":linux:build", ":macos:build")
}

tasks.register("testAll") {
    description = "Test for all platforms"
    group = "verification"
    dependsOn(":windows:test", ":linux:test", ":macos:test")
}

tasks.register("runAll") {
    description = "Cannot run for all platforms simultaneously. Use specific platform tasks."
    group = "application"
    doLast {
        println("Cannot run for all platforms. Use :windows:run, :linux:run, or :macos:run")
    }
}

// Wire build/test tasks to include all platforms
tasks.named("build") {
    dependsOn("buildAll")
}

tasks.named("check") {
    dependsOn("testAll")
}
