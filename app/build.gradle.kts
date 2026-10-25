plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.khuong.androidhomeskin"
    compileSdk = 34

    defaultConfig {
        applicationId = "cloud.khuong.androidhomeskin"
        minSdk = 24          // Android TV 7.0+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Deliberately dependency-free: framework views only, so the project
    // builds fast and the APK stays tiny.
}
