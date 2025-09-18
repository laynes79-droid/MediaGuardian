# Add project specific ProGuard rules here.
# By default, the flags in this file are applied to all build types.

# ProGuard rules for Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembers class kotlinx.coroutines.android.AndroidDispatcherFactory {
    private final java.lang.String coroutineName;
    private final java.lang.String dispatcherName;
}
-keep class kotlinx.coroutines.internal.MainDispatcherFactoryImpl {}
