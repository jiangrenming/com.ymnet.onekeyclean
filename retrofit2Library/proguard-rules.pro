# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\AndroidStudio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-ignorewarnings
-dontwarn
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
 -keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#不混淆泛型
-keepattributes Signature
#不混淆内部类
-keepattributes InnerClasses

#okio
-dontwarn okio.**
-keep class okio.**{*;}
-keep class okio.Okio{*;}
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

# rxjava rxandroid
-dontwarn rx.**
-dontwarn rx.android.**
-keep class rx.**{*;}
-keep class rx.android.**{*;}
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

#retrofit2
-dontwarn java.lang.invoke.*
-dontwarn retrofit2.**
-keep class retrofit2.**{*;}
-keepattributes Signature
-keepattributes Exceptions

# squareup
-dontwarn com.squareup.**
-keep class com.squareup.**{*;}
#okhttp3
-keep class okhttp3.** { *;}
-dontwarn sun.security.**
-keep class sun.security.** { *;}
-dontwarn okhttp3.**
#自己写的类
-keep class retrofit2_callback.* { *; }
-keep class retrofit2_callback.BaseCallModel{
  public *;
}
-keep class retrofit2_callback.MyCallBack{*;}
-keep class utils{*;}
-keep  class utils.ConvertParamsUtils{*;}
-keep class utils.HttpNetUtil{*;}
-keep public class receiver.NetWorkReceiver{*;}
-keep class utils{*;}
-keep class android.content.pm.** { *; }
-keep class android.test.mock.MockPackPackageManager{*;}


