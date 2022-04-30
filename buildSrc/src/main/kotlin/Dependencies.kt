
object Versions {
    const val compileSdk = 31
    const val minSdk = 21
    const val targetSdk = 31

    const val jvmTarget = "1.8"

    const val coreKtx = "1.7.0"
    const val appcompat = "1.4.1"
    const val constraintLayout = "2.1.2"
    const val material = "1.4.0"
    const val livedataKtx = "2.4.0"
    const val viewModelKtx = "2.4.1"
    const val activityKtx = "1.4.0"
    const val fragmentKtx = "1.4.1"
    const val lifecycleKtx = "2.4.1"

    const val flexbox = "3.0.0"
    const val mapbox = "10.4.0"
    const val gmsLocation = "18.0.0"
    const val coroutines = "1.5.2"
    const val okhttp = "4.9.0"
    const val okio = "2.10.0"
    const val retrofit = "2.9.0"
    const val coil = "1.4.0"
    const val refreshLayout = "2.0.5"
    const val mmkv = "1.2.13"
    const val appKtx = "1.0.0-alpha01"
    const val mvpKtx = "1.0.5"
    const val leakcanary = "2.9.1"
    const val docusign = "1.5.5"
    const val timber = "5.0.1"
}


object Dependencies {

    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val material = "com.google.android.material:material:${Versions.material}"
    const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.livedataKtx}"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModelKtx}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleKtx}"

    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLog = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val okio = "com.squareup.okio:okio:${Versions.okio}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val mmkv = "com.tencent:mmkv:${Versions.mmkv}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    // project
    const val liteMvp = ":lite-mvp"

    //debug
    const val leakcanary_android = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
}