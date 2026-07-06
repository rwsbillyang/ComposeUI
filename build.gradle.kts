// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
   
    alias(libs.plugins.kotlin.compose) apply false // 在这里统一声明 Compose 编译器插件，并设置不默认应用
    alias(libs.plugins.kotlin.ksp) apply false // KSP 属于第三方代码生成，它依然需要独立声明！
}