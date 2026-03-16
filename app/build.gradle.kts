plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias { libs.plugins.serialization }
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "iti.mad.dusk"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "iti.mad.dusk"
        minSdk = 26
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // My Dependencies
    implementation(libs.compose.material.icons.extended)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.room)
    implementation(libs.clarity)
    implementation(libs.dotenv)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.bundles.network)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    ksp(libs.room.compiler)
    ksp(libs.hilt.android.compiler)
    implementation(libs.play.services.location)
    implementation(libs.bundles.coil)

    implementation(libs.mapbox)

    implementation(libs.mapbox.compose) {
        exclude(group = "com.mapbox.common", module = "common")
    }

    implementation(libs.mapbox.search) {
        exclude(group = "com.mapbox.common", module = "common")
    }

    implementation(libs.mapbox.search.ui) {
        exclude(group = "com.mapbox.common", module = "common")
    }
}