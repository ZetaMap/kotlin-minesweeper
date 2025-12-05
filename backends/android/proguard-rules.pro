# ProGuard rules for Minesweeper
-keep class fr.zetamap.minesweeper.** { *; }
# Keep game classes

}
    kotlinx.serialization.KSerializer serializer(...);
-keepclasseswithmembers class kotlinx.serialization.json.** {
}
    *** Companion;
-keepclassmembers class kotlinx.serialization.json.** {

-dontnote kotlinx.serialization.AnnotationsKt
-keepattributes *Annotation*, InnerClasses
# Keep Kotlin metadata

-keepclassmembers class androidx.compose.** { *; }
-keep class androidx.compose.** { *; }
# Keep Compose


