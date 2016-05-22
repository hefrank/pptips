# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ~/software/sdk-as/tools/proguard/proguard-android.txt
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

#-dontwarn java.nio.file.Files
#-dontwarn java.nio.file.Path
#-dontwarn java.nio.file.OpenOption
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#add for greenDao
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public *;
}
#    public static java.lang.String TABLENAME;
-keep class **$Properties


-keepclassmembers class com.baidu.location {
   public *;
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#如果引用了v4或者v7包
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.app.Fragment


-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }



##移过来的混淆脚本#########################################################################
-printmapping "mapping.txt"
-repackageclasses ''
-allowaccessmodification
#-dontoptimize
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose
-dontusemixedcaseclassnames

-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.view.View {
  public *;
}


# wechat share	Sdk
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

#add for greenDao
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}

-keep class **$Properties


#start:从proguard.cfg合并过来
-ignorewarnings
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).

-dontoptimize
-dontpreverify

# Reduce the size of the output some more.

-repackageclasses 'contacts'
-allowaccessmodification

# Show line number when crash#

#-renamesourcefileattribute 360MobileSafe
#-keepattributes SourceFile,LineNumberTable

# RemoteViews might need annotations.

-keepattributes *Annotation*

# Preserve all fundamental application classes.

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve the special fields of all Parcelable implementations.

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve the required interface from the License Verification Library
# (but don't nag the developer if the library is not used at all).

-keep public interface com.android.vending.licensing.ILicensingService

-dontnote com.android.vending.licensing.ILicensingService

# The Android Compatibility library references some classes that may not be
# present in all versions of the API, but we know that's ok.

-dontwarn android.support.**

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# for third jar start
-keep class com.mapbar.android.location.** { *; }
-keep class com.weibo.net.** { *; }
# for third jar end

# for other start
-keepclassmembers class * extends android.app.Service {
    public int onStartCommand(...);
}
-keepclassmembers class * extends android.database.sqlite.SQLiteOpenHelper {
    public void onDowngrade(...);
}
-keep public class * implements android.os.Parcelable {
    public *;
}
-keep public class * implements java.io.Serializable {
    public *;
}

#end:从proguard.cfg合并过来
