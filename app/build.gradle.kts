plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.codemeric.musicplayer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.codemeric.musicplayer"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")

    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")


//    Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

//    Material UI
    implementation("com.google.android.material:material:1.12.0")

//Button Color
    implementation("androidx.palette:palette-ktx:1.0.0")

//    Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")
}