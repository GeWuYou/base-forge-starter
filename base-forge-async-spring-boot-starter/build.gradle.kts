dependencies {
    val libs = rootProject.libs

    // 项目依赖
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))

    // Spring Boot Starter AOP
    compileOnly(libs.springBootStarter.aop)

    // Kotlin 协程核心库
    compileOnly(libs.kotlinx.coroutines.core)
}