plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") // ✅ New required Compose plugin
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"

    id("com.google.gms.google-services")
}

android {
    namespace = "com.grocart.first"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.first"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13" // ✅ Must match your Compose BOM version
    }
}

dependencies {
    // Jetpack Compose BOM (Bill of Materials)
    // यह सभी Compose लाइब्रेरी के वर्शन को मैनेज करेगा।
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // Core & Lifecycle
    implementation("androidx.core:core-ktx")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.activity:activity-compose")

    // Compose UI & Material3
    // BOM का उपयोग करते समय वर्शन नंबर नहीं देना है।
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // सिर्फ एक बार इसे जोड़ें

    // Material Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3") // वर्शन को स्टेबल किया
    implementation("androidx.navigation:navigation-compose:2.7.7") // वर्शन को स्टेबल किया

    // Retrofit (API calls)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // स्टेबल वर्शन

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.6.0") // लेटेस्ट स्टेबल वर्शन
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(libs.androidx.recyclerview)
    implementation(libs.firebase.auth.ktx) // लेटेस्ट स्टेबल वर्शन

    // Debugging Tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}



