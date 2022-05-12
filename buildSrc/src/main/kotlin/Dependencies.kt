
object Versions {
    const val compileSdk = 31
    const val minSdk = 21
    const val targetSdk = 31

    const val jvmTarget = "1.8"

    const val coreKtx = "1.7.0"
    const val appcompat = "1.4.1"

    const val coroutines = "1.5.2"
    const val leakcanary = "2.9.1"
    const val timber = "5.0.1"
}


object Dependencies {

    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    // project
    const val liteMvp = ":lite-mvp"

    //debug
    const val leakcanary_android = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
}