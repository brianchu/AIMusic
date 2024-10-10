import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {

    val propertiesFiles = rootProject.file("apikey.properties")
    val apiProperties = Properties()
    apiProperties.load(FileInputStream(propertiesFiles))

    namespace = "com.example.musicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPENAI_KEY", apiProperties.getProperty("OPENAI_KEY", ""))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        buildConfig=true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.logging.interceptor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // view model
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // parsing and networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.moshi)  // pick your favour
    implementation(libs.moshi.kotlin)  // pick your favour
    implementation(libs.gson)

    // splash screen
    implementation(libs.core.splashscreen)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.accompanist.permissions)

    // Add this line to include the local .aar file
    implementation(libs.androidx.appcompat)

//    implementation(files("libs/musickitauth-release-1.1.2.aar"))
//    implementation(files("libs/mediaplayback-release-1.1.1.aar"))
}