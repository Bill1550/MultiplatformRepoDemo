import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
//    id("org.jetbrains.kotlin.native.cocoapods")
    id("kotlinx-serialization")
//    id("com.squareup.sqldelight")
}

//sqldelight {
//    database("JokesDatabase") {
//        packageName = "com.kurt.jokes"
//    }
//}

kotlin {
    android()

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.14.0")
        implementation("io.ktor:ktor-client-json:1.2.6")  // 1.3.0 may be current
        implementation("io.ktor:ktor-client-serialization:1.2.6")
        implementation("io.ktor:ktor-client-core:1.2.6")
        implementation("com.squareup.sqldelight:runtime:1.2.1")
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
