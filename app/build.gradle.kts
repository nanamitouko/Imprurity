plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("com.zty.test")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.yoruneko.example"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    flavorDimensions += "type"
    productFlavors {
        create("free") {
            dimension = "type"
        }
        create("paid") {
            dimension = "type"
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

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation(project(":annotation"))
    api("com.google.code.gson:gson:2.8.6")
}