
import org.gradle.kotlin.dsl.androidTest
import org.gradle.kotlin.dsl.test
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
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
    val koinVersion = "4.0.4"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    // coil
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")

    // room
    val roomVersion = "2.7.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)

    // navigation
    implementation(libs.androidx.navigation.compose)

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // algolia
    implementation("com.algolia:algoliasearch-client-kotlin:3.25.1")


    // Credential Manager libraries
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation("com.google.firebase:firebase-analytics")

    // datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // google accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    
    // Kotlin Parcel
//    implementation("org.jetbrains.kotlinx:kotlinx-parcelize-runtime:1.9.0")

    // ---- TESTING ----

    // JUnit4
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")

    // Roboelectric (for android-style unit test)
    testImplementation("org.robolectric:robolectric:4.15.1")

    // MockK
    testImplementation("io.mockk:mockk:1.14.5")                             // mocking
    androidTestImplementation("io.mockk:mockk-android:1.14.5")

    // Turbine
    testImplementation("app.cash.turbine:turbine:1.2.1")                     // Flow testing
    androidTestImplementation("app.cash.turbine:turbine:1.2.1")

    // Google Truth
    testImplementation("com.google.truth:truth:1.4.4")
    androidTestImplementation("com.google.truth:truth:1.4.4")

    // Coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2") // coroutine testing
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    // UI Automator
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")


    // --- Koin test support ---
    testImplementation("io.insert-koin:koin-test:4.1.0")
    testImplementation("io.insert-koin:koin-test-junit4:4.1.0")
    androidTestImplementation("io.insert-koin:koin-test:4.1.0")
    androidTestImplementation("io.insert-koin:koin-test-junit4:4.1.0")

    // --- Android instrumentation / UI tests (optional if you need them) ---
    androidTestImplementation("androidx.test:core:1.7.0")


    // Jetpack Compose UI testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.8.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.3")

}
