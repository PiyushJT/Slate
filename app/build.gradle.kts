plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.piyushjt.slate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.piyushjt.slate"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    //noinspection UseTomlInstead
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-database")



    //noinspection UseTomlInstead
    implementation("androidx.core:core-splashscreen:1.0.1")  // Splash screen api
    //noinspection UseTomlInstead,GradleDependency
    implementation("com.google.android.material:material:1.3.0-alpha03")  // Floating button
}