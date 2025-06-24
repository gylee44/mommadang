plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    viewBinding{
        enable = true
    }



    namespace = "com.tukorea.mommadang"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tukorea.mommadang"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // FireBase
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore:24.7.0")

    //안드로이드 및 기본 라이브러리
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)

    // Glide - 이미지 로딩
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation("androidx.viewpager2:viewpager2:1.0.0")  // view2 사용
    implementation("com.github.bumptech.glide:glide:4.16.0")    // Glide 사용
    kapt("com.github.bumptech.glide:compiler:4.16.0")  // 코틀린 KAPT 사용 시
    implementation("jp.wasabeef:glide-transformations:4.3.0")   // Glide의 사진 편집
    implementation ("com.google.android.material:material:1.10.0")  // MaterialCardView사용
    implementation("com.naver.maps:map-sdk:3.22.0")
}