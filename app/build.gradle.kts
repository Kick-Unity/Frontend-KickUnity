plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {

    namespace = "com.example.kickunity"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kickunity"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")

    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.8.0")
    implementation("com.squareup.retrofit2:converter-gson:2.8.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    // Firebase BOM 의존성 관리
    implementation("com.google.firebase:firebase-bom:33.6.0")

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx:22.1.2")

    // Firebase Authentication (로그인 기능 추가)
    implementation("com.google.firebase:firebase-auth-ktx:21.0.5")

    // Firebase Firestore (실시간 데이터베이스 연동)
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")

    // Firebase Realtime Database (사용시 추가)
    implementation("com.google.firebase:firebase-database-ktx:20.0.5")

    // Firebase Cloud Messaging (푸시 알림 기능 추가)
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.0")
}
