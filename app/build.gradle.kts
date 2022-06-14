val activity = "1.2.3"
val fragment = "1.3.5"
val archLifecycle = "2.3.1"
val navigationComponent = "2.3.5"
val playServices = "18.0.0"
val koin = "3.2.0"

plugins {
    id("com.android.application")
    `kotlin-android`
    `kotlin-parcelize`
    `kotlin-kapt`
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.husseinelfeky.smartbank"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")

    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")

    implementation("androidx.activity:activity-ktx:$activity")
    implementation("androidx.fragment:fragment-ktx:$fragment")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$archLifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$archLifecycle")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigationComponent")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationComponent")
    testImplementation("androidx.navigation:navigation-testing:$navigationComponent")

    implementation("com.google.android.gms:play-services-location:$playServices")

    implementation("io.insert-koin:koin-android:$koin")
    testImplementation("io.insert-koin:koin-test-junit4:$koin")

    implementation(platform("com.google.firebase:firebase-bom:30.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.github.chintan369:Geo-FireStore-Query:1.1.0")

    retrofitDependencies(true)
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("com.github.harrisonsj:KProgressHUD:1.1")

    implementation("com.jakewharton.timber:timber:4.7.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
