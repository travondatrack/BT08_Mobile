plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

import java.util.Properties

android {
    namespace = "com.example.myapplication"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    val localProperties = Properties()
    val lp = rootProject.file("local.properties")
    if (lp.exists()) {
        lp.inputStream().use { localProperties.load(it) }
    }
    val azureSas = localProperties.getProperty("AZURE_SAS_URL") ?: ""

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose the AZURE SAS URL via BuildConfig (keep actual value out of VCS by using local.properties)
        buildConfigField("String", "AZURE_SAS_URL", "\"$azureSas\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    dependenciesInfo {
        includeInApk = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // HTTP client for Azure uploads
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
