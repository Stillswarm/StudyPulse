# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep data/model classes for Firestore reflective serialization/deserialization
-keep class com.studypulse.**.model.** { *; }
-keep class com.studypulse.**.data.**Dto { *; }

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Gson/JSON serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Room Database
# Room/WorkManager instantiate their generated *_Impl databases reflectively
# via Class.getDeclaredConstructor(); R8 full mode must keep the no-arg
# constructor or startup crashes with NoSuchMethodException: WorkDatabase_Impl.<init> [].
-keep class * extends androidx.room.RoomDatabase {
    void <init>();
}
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    void <init>();
}
-dontwarn androidx.room.paging.**

# WorkManager generated Room database & AndroidX Startup
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
}
-keep class androidx.startup.StartupException { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Firebase rules
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Authentication
-keep class com.google.firebase.auth.** { *; }
-keepclassmembers class com.google.firebase.auth.** { *; }

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** { *; }
-keep class com.google.protobuf.** { *; }

# Firebase Storage
-keep class com.google.firebase.storage.** { *; }

# Firebase Messaging/FCM
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }

# Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-keepattributes SourceFile,LineNumberTable

# Keep your model classes for Firebase
# These are crucial for Firebase serialization/deserialization (including Kotlin property getters)
-keep class **.model.** {
    *;
}
-keep class **.*Dto {
    *;
}

# Keep classes that use @Keep annotation
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}