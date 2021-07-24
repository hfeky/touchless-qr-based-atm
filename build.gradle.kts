// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        // Android Gradle Build Tools
        classpath("com.android.tools.build:gradle:4.2.2")

        // Kotlin Gradle Plugin
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))

        // Nav Safe Args Plugin
        classpath(Dependencies.Gradle.androidXNavSafeArgs)

        // Google Services
        classpath("com.google.gms:google-services:4.3.8")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
