import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.pos.ricoybakeshop"
    compileSdk = 35
    android.buildFeatures.buildConfig = true;

    defaultConfig {
        applicationId = "com.pos.ricoybakeshop"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        val anonKey = properties.getProperty("SUPABASE_ANON_KEY") ?: ""

        buildConfigField(
            type = "String",
            name = "SUPABASE_ANON_KEY",
            value = anonKey
        )

        val apiKey = properties.getProperty("SUPABASE_URL") ?: ""

        buildConfigField(
            type = "String",
            name = "SUPABASE_URL",
            value = apiKey
        )

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // material design
    implementation("com.google.android.material:material:1.14.0-alpha01")
    //local database
    implementation("androidx.room:room-runtime:2.7.1")
    annotationProcessor("androidx.room:room-compiler:2.7.1")
    // http request with supabase
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303")
    // password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}