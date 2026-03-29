
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
}

val secrets = Properties()
secrets.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.studypulse.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.studypulse.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "ALGOLIA_APP_ID",
                "\"${secrets.getProperty("algolia.appId")}\""
            )
            buildConfigField(
                "String",
                "ALGOLIA_SEARCH_KEY",
                "\"${secrets.getProperty("algolia.searchKey")}\""
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isDebuggable = true
            buildConfigField(
                "String",
                "ALGOLIA_APP_ID",
                "\"${secrets.getProperty("algolia.appId")}\""
            )
            buildConfigField(
                "String",
                "ALGOLIA_SEARCH_KEY",
                "\"${secrets.getProperty("algolia.searchKey")}\""
            )
        }
    }
    packaging {
        resources {
            pickFirsts += "META-INF/LICENSE.md"
            pickFirsts += "META-INF/LICENSE-notice.md"
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
        buildConfig = true
    }
}

dependencies {

    // project
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:nav"))
    implementation(project(":core:user"))
    implementation(project(":core:semester"))
    implementation(project(":core:firebase"))

    implementation(project(":feat:user"))
    implementation(project(":feat:auth"))
    implementation(project(":feat:semester"))
    implementation(project(":feat:attendance"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)

    // navigation
    implementation(libs.androidx.navigation.compose)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.auth)

    // algolia
    implementation(libs.algoliasearch.client.kotlin)

    // crashlytics
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.google.firebase.analytics)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    // google accompanist
    implementation(libs.accompanist.systemuicontroller)
    
    // Kotlin Parcel
//    implementation("org.jetbrains.kotlinx:kotlinx-parcelize-runtime:1.9.0")

    // ---- TESTING ----

    // JUnit4
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v130)
    androidTestImplementation(libs.androidx.runner)

    // Roboelectric (for android-style unit test)
    testImplementation(libs.robolectric)

    // MockK
    testImplementation(libs.mockk)                             // mocking
    androidTestImplementation(libs.mockk.android)

    // Turbine
    testImplementation(libs.turbine)                     // Flow testing
    androidTestImplementation(libs.turbine)

    // Google Truth
    testImplementation(libs.truth)
    androidTestImplementation(libs.truth)

    // Coroutines
    testImplementation(libs.jetbrains.kotlinx.coroutines.test) // coroutine testing
    androidTestImplementation(libs.jetbrains.kotlinx.coroutines.test)

    // Espresso
    androidTestImplementation(libs.androidx.espresso.core.v370)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.androidx.espresso.intents)

    // UI Automator
    androidTestImplementation(libs.androidx.uiautomator)

    // --- Koin test support ---
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)
    androidTestImplementation(libs.koin.test.junit4)

    // --- Android instrumentation / UI tests (optional if you need them) ---
    androidTestImplementation(libs.androidx.core)


    // Jetpack Compose UI testing
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}
