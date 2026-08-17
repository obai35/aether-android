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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Room entities
-keep @androidx.room.Entity class *
-keep class * extends androidx.room.Entity

# Keep Hilt components
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.** class *

# Keep Moshi adapters
-keep class ** extends com.squareup.moshi.JsonAdapter

# Keep Retrofit interfaces
-keep interface retrofit2.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }