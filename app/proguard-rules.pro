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

-keepclassmembers class ly.pp.justpiano3.utils.MidiDeviceUtil {
    void onMidiMessageReceive(...);
}
-keep class javazoom.jl.decoder.JavaLayerUtils{*;}
-keep class io.netty.**{*;}
-keep class protobuf.**{*;}
-keep class com.google.firebase.crashlytics.buildtools.reloc.org.**{*;}

-keepattributes Signature
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
!static !transient <fields>;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepattributes InnerClasses

-dontwarn com.aayushatharva.brotli4j.**
-dontwarn com.barchart.udt.**
-dontwarn com.fasterxml.aalto.**
-dontwarn com.github.luben.zstd.**
-dontwarn com.google.protobuf.nano.**
-dontwarn com.jcraft.jzlib.**
-dontwarn com.ning.compress.**
-dontwarn com.oracle.svm.core.annotate.**
-dontwarn com.sun.nio.sctp.**
-dontwarn gnu.io.**
-dontwarn io.netty.internal.tcnative.**
-dontwarn javax.naming.**
-dontwarn javax.xml.stream.**
-dontwarn lzma.sdk.**
-dontwarn net.jpountz.lz4.**
-dontwarn net.jpountz.xxhash.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.eclipse.jetty.alpn.**
-dontwarn org.eclipse.jetty.npn.**
-dontwarn org.jboss.marshalling.**
-dontwarn org.slf4j.**
-dontwarn reactor.blockhound.**
-dontwarn sun.security.x509.**