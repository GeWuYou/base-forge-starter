plugins {
    // 基本 Java 支持
    alias(libs.plugins.java)
    // Java 库开发支持
    alias(libs.plugins.java.library)
    // Maven 发布支持
    alias(libs.plugins.maven.publish)
    // Spring 依赖管理插件，简化依赖版本管理
    alias(libs.plugins.spring.dependency.management)
    // Kotlin 支持
    alias(libs.plugins.kotlin.jvm)
    // Kotlin Spring 支持
    alias(libs.plugins.kotlin.plugin.spring)
    // Kotlin Lombok 支持
    alias(libs.plugins.kotlin.plugin.lombok)
}
/**
 * 由于 Kotlin 插件被引入时会自动添加依赖，但根项目不需要 Kotlin 依赖，因此需要排除 Kotlin 依赖
 */
configurations.implementation {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
}

val configDir = "$rootDir/config/"
val tasksDir = "$configDir/tasks/"
apply {
    from(file("$tasksDir/gradleTask.gradle"))
}
subprojects {
    val libs = rootProject.libs
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.jetbrains.kotlin.plugin.lombok")
        // 导入仓库配置
        from(file("$configDir/repositories.gradle"))
        // 导入源代码任务
        from(file("$tasksDir/sourceTask.gradle"))
        // 导入发布配置
        from(file("$configDir/publishing.gradle"))
    }
    dependencies {
        implementation(platform(libs.springBootDependencies.bom))
        implementation(platform(libs.grpc.bom))
        implementation(platform(libs.kotlin.bom))
        implementation(platform(libs.springCloudDependencies.bom))

        implementation(libs.springBootStarter)
        compileOnly(libs.springBootStarter.web)
        compileOnly(libs.lombok)
        annotationProcessor(libs.lombok)
        annotationProcessor(libs.springBoot.configuration.processor)

    }
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
    tasks.withType<Jar> {
        isEnabled = true
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}


