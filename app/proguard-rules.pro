# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Room keeps
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase

