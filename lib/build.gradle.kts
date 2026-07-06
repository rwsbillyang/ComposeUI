plugins {
    // 1. 现代 AGP 9+ 原生内置支持 Kotlin 编译，移除传统的 kotlin.android 插件
    //// 这个是外部第三方/Google提供的插件，需要指定版本号。  版本号写在 libs.versions.toml 里面，所以用 alias。
    alias(libs.plugins.android.library)

    // 2. 现代 Compose 官方编译器插件，版本号在 TOML 中直接绑定并复用 Kotlin 2.4.0
    alias(libs.plugins.kotlin.compose)

    // 这个是 Gradle 软件自身纯内置的核心功能，不需要从网上下载，更不需要版本号。 没必要写进 toml 文件，因此直接用 id 引入最干净。
    id("maven-publish") // 引入 Maven 发布插件
}

android {
    namespace = "com.github.rwsbillyang.composeui"
    
    // 4. TOML Provider 强类型数值转换
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        minSdk = 26
        //从 AGP 8.0/9.0+ 开始，Android 官方彻底取消了 Library（库）模块的 targetSdk 和 versionCode / versionName 属性。
        //targetSdk = libs.versions.targetSdkVersion.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    // 5. 对齐现代 AGP 9.2 编译生态，强烈建议升级至现代的 JavaVersion.VERSION_21
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 6. Built-in Kotlin 指定编译目标的方式
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            // 自动将源码打包为 -sources.jar（AGP 独有便捷 API）
            withSourcesJar()
            // 如果需要文档，也可以开启 javadoc
            // withJavadocJar()
        }
    }
}

dependencies {
    // 依赖项切换为 KTS 强类型小括号语法
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    //  compileOnly(libs.androidx.compose.material3.window.sizeclass)
    implementation(libs.androidx.compose.material.icons.extended) // 解决 Bonsai 相关 issue
}



//./gradlew :lib:publishToMavenLocal
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            // 指定 ArtifactId。GroupId 和 Version 默认继承外层的 group 和 version
            groupId = "com.github.rwsbillyang"
            artifactId = "composeui"
            version = libs.versions.composeuiVersion.get()

            // 核心：在 afterEvaluate 阶段或通过延迟查找，将 AGP 生成的 release 组件传入
            // AGP 9.x 依然通过 components["release"] 暴露给 Maven 插件
            afterEvaluate {
                from(components["release"])
            }

            // 可选：如果生成的 POM 文件需要特定的元数据，可以在这里补充
            pom {
                name.set("Android Compose UI components Library")
                description.set("A basic Compose UI components library  tailored for AGP 9.x")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}