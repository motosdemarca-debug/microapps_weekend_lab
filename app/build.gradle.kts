plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.dayssince"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dayssince"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    // 🔐 Firma release (rellena tus contraseñas reales o usa Generate Signed Bundle)
    signingConfigs {
        create("release") {
            // Si el .jks está en la raíz del proyecto:
            storeFile = file("dayssince-release.jks")
            storePassword = "REEMPLAZA_CON_TU_PASSWORD"
            keyAlias = "dayssince"
            keyPassword = "REEMPLAZA_CON_TU_PASSWORD"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // sin firma especial
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    // Con el plugin 'kotlin-compose' no necesitas fijar manualmente el compiler extension version.
    // composeOptions { kotlinCompilerExtensionVersion = "x.y.z" }

    compileOptions {
        // ✅ Mantén Java 21 para evitar el mismatch Kotlin/Java
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        // Para usar java.time en minSdk bajos:
        isCoreLibraryDesugaringEnabled = true
    }

    // Asegura toolchain y bytecode a 21
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM (desde tu version catalog)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose UI/M3
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Test/Debug
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 🔧 Desugaring (para java.time)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Extras que estás usando y no están en el catalog:
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.code.gson:gson:2.10.1")
}
