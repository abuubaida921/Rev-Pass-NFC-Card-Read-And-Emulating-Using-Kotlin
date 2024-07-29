import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets

plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
}

android {
    namespace = "com.example.rev_pass_testing_using_metrodroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rev_pass_testing_using_metrodroid"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
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
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.multidex)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kotlin {
//    targets {
//        fromPreset(presets.android, 'android')
//        fromPreset(presets.jvm, 'jvmCli') {
//            compilations.all {
//                tasks[compileKotlinTaskName].dependsOn += generateLocalize
//                kotlinOptions {
//                    jvmTarget = "9"
//                }
//            }
//        }
//
////        final def iOSTarget = System.getenv('SDK_NAME')?.startsWith("iphoneos")  ? presets.iosArm64 : presets.iosX64
//
////        fromPreset(iOSTarget, 'iOS') {
////            binaries {
////                framework("metrolib") {
////                    linkTask.dependsOn += 'generateLocalize'
////                    linkTask.dependsOn += 'iOSLanguages'
////                }
////            }
////            binaries.all {
////                linkerOpts new File(project('proto').buildDir, 'libs/main/static/libmain.a').path
////            }
////
////            compilations.all {
////                tasks[compileKotlinTaskName].dependsOn += 'generateLocalize'
////                tasks[compileKotlinTaskName].dependsOn += 'iOSLanguages'
////                tasks[compileKotlinTaskName].dependsOn += ':proto:mainStaticLibrary'
////                cinterops {
////                    stations {
////                        defFile project.file("src/iOSMain/cinterop/stations.def")
////                        includeDirs(new File(project('proto').buildDir, 'generated/source/proto/main/objc'))
////                        compilerOpts '-I' + projectDir +
////                        '/third_party/protobuf/objectivec', '-DTARGET_OS_OSX=0',
////                        '-DTARGET_OS_MACCATALYST=0', '-DNS_FORMAT_ARGUMENT(A)=', '-D_Nullable_result=_Nullable'
////                    }
////                }
////            }
////        }
//    }
}