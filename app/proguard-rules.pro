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

# Android SearchView
-keep class android.support.v7.widget.SearchView { *; }

# DBFlow - https://github.com/Raizlabs/DBFlow/issues/248
-keep class com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder

# Proguard - http://square.github.io/retrofit/
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn okio.**

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
##---------------End: proguard configuration for Gson  ----------

# Zxing - https://github.com/journeyapps/zxing-android-embedded
# It looks not necessary

# Stetho - https://github.com/facebook/stetho/tree/master/stetho-js-rhino#proguard
-keep class com.facebook.stetho.** { *; }

# Rhino (javascript) - https://github.com/facebook/stetho/tree/master/stetho-js-rhino#proguard
-dontwarn org.mozilla.javascript.**
-dontwarn org.mozilla.classfile.**
-keep class org.mozilla.classfile.** { *; }
-keep class org.mozilla.javascript.* { *; }
-keep class org.mozilla.javascript.annotations.** { *; }
-keep class org.mozilla.javascript.ast.** { *; }
-keep class org.mozilla.javascript.commonjs.module.** { *; }
-keep class org.mozilla.javascript.commonjs.module.provider.** { *; }
-keep class org.mozilla.javascript.debug.** { *; }
-keep class org.mozilla.javascript.jdk13.** { *; }
-keep class org.mozilla.javascript.jdk15.** { *; }
-keep class org.mozilla.javascript.json.** { *; }
-keep class org.mozilla.javascript.optimizer.** { *; }
-keep class org.mozilla.javascript.regexp.** { *; }
-keep class org.mozilla.javascript.serialize.** { *; }
-keep class org.mozilla.javascript.typedarrays.** { *; }
-keep class org.mozilla.javascript.v8dtoa.** { *; }
-keep class org.mozilla.javascript.xml.** { *; }
-keep class org.mozilla.javascript.xmlimpl.** { *; }

# Crashlytics - https://docs.fabric.io/android/crashlytics/dex-and-proguard.html
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable,*Annotation*

-keep public class * extends java.lang.Exception

# RxJava - https://github.com/artem-zinnatullin/RxJavaProGuardRules/blob/master/rxjava-proguard-rules/proguard-rules.txt
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Guava 19.0 - https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-guava.pro
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Retrolambda - https://github.com/evant/gradle-retrolambda
-dontwarn java.lang.invoke.*