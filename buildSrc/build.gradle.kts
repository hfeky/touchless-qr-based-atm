plugins {
    /**
     * [Kotlin DSL Plugin]
     * (https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin)
     */
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation("com.android.tools.build:gradle:4.2.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
}
