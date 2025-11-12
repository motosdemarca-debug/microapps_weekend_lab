import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.dayssince" // cámbialo cuando decidas tu paquete final
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dayssince" // idem
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    // --- Firma opcional (solo si existe keystore local) ---
    val keystorePropsFile = rootProject.file("keystore.properties")
    val hasKeystore = keystorePropsFile.exists()
    val keystoreProps = Properties().apply {
        if (hasKeystore) load(keystorePropsFile.inputStream())
    }

    signingConfigs {
        if (hasKeystore) {
            create("release") {
                storeFile = file(keystoreProps["storeFile"] ?: "")
                storePassword = (keystoreProps["storePassword"] ?: "") as String
                keyAlias = (keystoreProps["keyAlias"] ?: "") as String
                keyPassword = (keystoreProps["keyPassword"] ?: "") as String
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // En CI (GitHub Actions) NO firmamos → AAB sin firmar (Play firmará).
            val isCi = System.getenv("CI")?.toBoolean() == true
            if (hasKeystore && !isCi) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // Desugaring para java.time y APIs Java 8+ en minSdk 24
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    // Kotlin 2.0.x + Compose Compiler recomendado
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.2"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Compose BOM ---
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))

    // --- Compose core ---
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // --- AndroidX + Lifecycle + Activity Compose ---
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")

    // --- DataStore (persistencia local) ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Desugaring (java.time, etc.) ---
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // --- Tests ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
