# ProGuard / R8 optimization rules for DailyTask

# Keep Lottie Animation classes
-keep class com.airbnb.lottie.** { *; }

# Keep Room Database Entities & DAOs
-keep class com.example.dailytask.data.model.** { *; }
-keep class com.example.dailytask.data.local.** { *; }

# Keep Compose Runtime
-keep class androidx.compose.runtime.** { *; }
