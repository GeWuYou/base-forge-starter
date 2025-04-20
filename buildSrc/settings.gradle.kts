// buildSrc/settings.gradle.kts
pluginManagement {
    repositories {
        mavenLocal()          // 从本地仓库获取插件
        gradlePluginPortal()  // 从 Gradle 插件门户获取插件
        mavenCentral()        // 从 Maven 中央仓库获取依赖
    }
    plugins {
        // 解决 JDK 工具链问题
        // https://mvnrepository.com/artifact/org.gradle.toolchains/foojay-resolver
        id("org.gradle.toolchains.foojay-resolver") version "0.9.0"
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
rootProject.name = "buildSrc"