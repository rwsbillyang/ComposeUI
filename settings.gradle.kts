pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

        maven { url = uri( "https://mirrors.cloud.tencent.com/gradle/")}
        maven { url = uri( "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven{ url = uri( "https://maven.aliyun.com/nexus/content/groups/public/")}
        maven { url = uri( "https://maven.aliyun.com/repository/google/") }
        maven { url = uri( "https://maven.aliyun.com/repository/jcenter/")}
        maven { url = uri("https://jitpack.io") }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()

        maven { url = uri( "https://mirrors.cloud.tencent.com/gradle/")}
        maven { url = uri( "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven{ url = uri( "https://maven.aliyun.com/nexus/content/groups/public/")}
        maven { url = uri( "https://maven.aliyun.com/repository/google/") }
        maven { url = uri( "https://maven.aliyun.com/repository/jcenter/")}
        maven { url = uri("https://jitpack.io") }
    }

    versionCatalogs {
        create("libs") {
            from(files("../../libs.versions.toml"))
            //from("com.github.rwsbillyang:version-catalog:1.0.2")
        }
    }
}

rootProject.name = "CompposeUI"
include(":app")
include(":lib")
