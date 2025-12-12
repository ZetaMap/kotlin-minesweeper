-dontobfuscate

-keep class fr.zetamap.minesweeper.** { *; }
-keep class androidx.compose.** { *; }

-keepattributes *Annotation*, InnerClasses
-keepclassmembers class androidx.compose.** { *; }

#-printusage out.txt
