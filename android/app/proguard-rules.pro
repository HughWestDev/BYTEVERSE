-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class dev.west.byteverse.data.remote.**$$serializer { *; }
-keepclassmembers class dev.west.byteverse.data.remote.** {
    *** Companion;
}
-keep interface dev.west.byteverse.data.remote.GitHubApiService { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
