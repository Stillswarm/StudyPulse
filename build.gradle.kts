
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id("com.google.firebase.crashlytics") version "3.0.4" apply false
    alias(libs.plugins.kotlin.serialization) apply false  // ← add
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.2.0" apply false  // ← add

    alias(libs.plugins.android.library) apply false
}