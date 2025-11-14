plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

//android {
//    namespace = "com.example.edgevisionrt"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.example.edgevisionrt"
//        minSdk = 24
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//
//        ndk {
//            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
//        }
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//
//    // Native build configuration (only once!)
//    externalNativeBuild {
//        cmake {
//            path = file("CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
//
//    // Copy OpenCV native libraries to jniLibs
//    sourceSets {
//        getByName("main") {
//            jniLibs.srcDirs("../opencv/native/libs")
//        }
//    }
//
//    // Packaging options for native libraries
//    packaging {
//        jniLibs {
//            useLegacyPackaging = true
//        }
//    }
//}
//
//dependencies {
//    implementation("androidx.core:core-ktx:1.13.1")
//    implementation("androidx.appcompat:appcompat:1.7.0")
//    implementation("com.google.android.material:material:1.12.0")
//    implementation("androidx.activity:activity-ktx:1.9.2")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.2.1")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
//}

android {
    namespace = "com.example.edgevisionrt"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.edgevisionrt"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add NDK config
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.22.1"
        }
    }

    // Add this: Copy OpenCV native libraries to jniLibs
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("../opencv/native/libs")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}