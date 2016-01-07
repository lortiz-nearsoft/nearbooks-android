# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/epool/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class title to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# DBFlow - https://github.com/Raizlabs/DBFlow/issues/248
-keep class com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder

# Proguard - http://square.github.io/retrofit/
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn okio.**

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

# Proguard - https://github.com/bumptech/glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Gson - https://github.com/google/gson
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------


# Zxing - https://github.com/journeyapps/zxing-android-embedded
# It looks not necessary

# Stetho - https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-facebook-stetho.pro
-keep class com.facebook.stetho.** { *; }

# Rhino (javascript) - https://github.com/facebook/stetho/tree/master/stetho-js-rhino#proguard
-dontwarn org.mozilla.javascript.**
-dontwarn org.mozilla.classfile.**
-keep class org.mozilla.javascript.** { *; }