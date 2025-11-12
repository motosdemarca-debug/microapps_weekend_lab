plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Requerido con Kotlin 2.x si compose = true
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.dayssince" // <-- ajusta a tu paquete
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dayssince" // <-- ajusta si quieres
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
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

    buildFeatures { compose = true }

    // Con el plugin de Compose para Kotlin 2.x ya NO se define composeOptions.kotlinCompilerExtensionVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Compose BOM + UI ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // --- Material ---
    implementation(libs.androidx.compose.material3)
    // Usamos coordenadas directas para evitar el alias que te falla
    implementation("androidx.compose.material:material-icons-extended")

    // --- Activity + Lifecycle (Compose ViewModel) ---
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Igual: coordenadas directas para evitar el alias que te falla
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)

    // --- Core ---
    implementation(libs.androidx.core.ktx)

    // --- Tests ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- Debug tools ---
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
