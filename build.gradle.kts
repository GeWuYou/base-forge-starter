plugins {
    // 基本 Java 支持
    alias(libs.plugins.java)
    // Java 库开发支持
    alias(libs.plugins.javaLibrary)
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
allprojects {
    // 设置全局属性
    ext {
        set(ProjectFlags.USE_SPRING_BOOT, true)
        set(ProjectFlags.USE_GRPC, false)
    }
}
subprojects {
    afterEvaluate {
        if (project.getPropertyByBoolean(ProjectFlags.USE_GRPC)) {
            dependencies{
                implementation(platform(libs.grpc.bom))
                // gRPC Stub
                implementation(libs.grpc.stub)
            }
        }
        if (project.getPropertyByBoolean(ProjectFlags.USE_SPRING_BOOT)){
            dependencies{
                implementation(platform(libs.springBootDependencies.bom))
                compileOnly(libs.springBootStarter.web)
            }
        }
    }
    val libs = rootProject.libs
    apply {
        plugin(libs.plugins.javaLibrary.get().pluginId)
        plugin(libs.plugins.java.get().pluginId)
        plugin(libs.plugins.maven.publish.get().pluginId)
        plugin(libs.plugins.spring.dependency.management.get().pluginId)
        plugin(libs.plugins.kotlin.jvm.get().pluginId)
        plugin(libs.plugins.kotlin.plugin.spring.get().pluginId)
        plugin(libs.plugins.kotlin.plugin.lombok.get().pluginId)
        // 导入仓库配置
        from(file("$configDir/repositories.gradle"))
        // 导入源代码任务
        from(file("$tasksDir/sourceTask.gradle"))
        // 导入发布配置
        from(file("$configDir/publishing.gradle"))
    }
    dependencies {
        implementation(platform(libs.kotlin.bom))
        implementation(platform(libs.springCloudDependencies.bom))
        annotationProcessor(libs.springBoot.configuration.processor)
        compileOnly(libs.lombok)
        annotationProcessor(libs.lombok)
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

fun Project.getPropertyByBoolean(key: String): Boolean {
    return properties[key]?.toString()?.toBoolean() ?: false
}
