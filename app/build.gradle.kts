plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.week14.gmipathway"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.week14.gmipathway"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Lists English and Bahasa Melayu in the phone's per-app language settings.
    androidResources {
        generateLocaleConfig = true
    }
}

// The unit tests read courses.json directly, so changing it must run them again.
tasks.withType<Test>().configureEach {
    inputs.dir("src/main/assets")
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.core.splashscreen)
    implementation(libs.fragment)
    implementation(libs.gson)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    testImplementation(libs.junit)
}