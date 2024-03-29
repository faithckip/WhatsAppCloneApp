plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("dagger.hilt.android.plugin")
    //id("com.google.devtools.ksp")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.whatsappcloneapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.whatsappcloneapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.navigation:navigation-runtime-ktx:2.7.6")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")

    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("com.google.firebase:firebase-firestore:24.10.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation ("io.coil-kt:coil-compose:2.5.0")
    implementation ("androidx.navigation:navigation-compose:2.7.6")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("com.google.dagger:hilt-android:2.48.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.compose.material:material-icons-extended-android:1.5.4")
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")

    //use KSP for Dagger Hilt
    //ksp("com.google.dagger:hilt-compiler:2.48.1")
    //ksp("androidx.hilt:hilt-compiler:1.1.0")
    implementation ("com.google.dagger:dagger:2.48.1")

    kapt ("com.google.dagger:hilt-android-compiler:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    //ksp("androidx.room:room-compiler:2.44.2")
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-compiler
    //runtimeOnly("com.google.dagger:hilt-compiler:2.48.1")

    //REtrofit
    //implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //implementation("com.sqaureup.okhttp3:logging-interceptor:4.10.0")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Add the dependencies for the App Check libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Allow references to generated code

}