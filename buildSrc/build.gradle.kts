// buildSrc/build.gradle.kts
plugins {
    // 核心：启用 Kotlin DSL 支持
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.javaGradle.plugin)
}
dependencies {
}
gradlePlugin {
    plugins {

    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
repositories {
    mavenLocal()          // 从本地仓库获取插件
    gradlePluginPortal()  // 从 Gradle 插件门户获取插件
    mavenCentral()        // 从 Maven 中央仓库获取依赖
}