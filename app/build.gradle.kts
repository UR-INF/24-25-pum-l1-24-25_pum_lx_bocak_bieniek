plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.focuszone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.focuszone"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // JUnit for assertions
    testImplementation(libs.junit)

    // Truth for better assertions
    testImplementation("com.google.truth:truth:1.4.4")

    // JUnit for assertions
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // AndroidX Test framework
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // Android Test UI automation framework
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")

    // Biometric APIs
    implementation("androidx.biometric:biometric:1.1.0")

    // Crypto APIs
    implementation("androidx.security:security-crypto:1.0.0")

    // Json serializer
    implementation("com.google.code.gson:gson:2.10")

    // OkHttp client - used as http interceptor
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}