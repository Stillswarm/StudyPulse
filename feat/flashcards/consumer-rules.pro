# WorkManager uses Room 2.6.x internally, whose consumer rules omit `<init>()` under R8 full mode.
-keep class * extends androidx.room.RoomDatabase { void <init>(); }
-keep class androidx.work.impl.WorkDatabase_Impl { public <init>(); }

# Firestore DTOs used via reflection (CustomClassMapper)
-keep class com.studypulse.feat.flashcards.data.FlashcardDto { *; }
-keep class com.studypulse.feat.flashcards.domain.model.** { *; }
