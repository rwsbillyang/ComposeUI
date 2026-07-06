plugins {
    // 1. 现代 AGP 9+ 原生内置支持 Kotlin 编译，移除传统的 kotlin.android 插件
    alias(libs.plugins.android.application)
    
    // 2. 引入官方现代 Compose 编译器插件（其版本在 TOML 中直接复用 Kotlin 2.4.0 的版本）
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.github.rwsbillyang.composeui.example"
    
    // 3. TOML Provider 强类型数值转换
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "com.github.rwsbillyang.composeui.example"
        minSdk = 26
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                "proguard-rules.pro"
            )
        }
    }

    // 4. 对齐现代 AGP 9.2 编译生态，全面升级编译目标至现代标准的 JDK 21（或 17）
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
    
    // 6. ❌ 核心改变：旧版 composeOptions { kotlinCompilerExtensionVersion = ... } 必须彻底删掉！

    // 7. 修正过时的 packagingOptions 语法为现代标准的 packaging 闭包
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
//  Built-in Kotlin 指定编译目标的方式
kotlin {
    jvmToolchain(21)
    compilerOptions {
        // 比如开启一些实验性特性
        // freeCompilerArgs.add("-Xcontext-receivers")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // 基础核心依赖
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    // 现代 Compose 依赖 (转为 KTS 强类型小括号语法)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window.sizeclass)
    // implementation(libs.androidx.compose.material)
    implementation(libs.androidx.activity.compose) // RoutableActivity

    // 调试期辅助与单元测试
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)

    // 业务库依赖
    implementation(libs.composerouter)
    
    // 8. 现代 KTS 引入本地子模块的标准简写
    implementation(project(":lib"))
}