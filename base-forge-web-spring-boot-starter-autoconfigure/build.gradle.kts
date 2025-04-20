dependencies {
    val libs = rootProject.libs

    // Base Forge Modules (Compile Only)
    // 国际化模块
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))

    // 工具模块
    compileOnly(project(Modules.UTIL.SPRING_BOOT_STARTER))

    // Redis模块
    compileOnly(project(Modules.REDIS.SPRING_BOOT_STARTER))

    // JSON处理模块
    compileOnly(project(Modules.JSON.SPRING_BOOT_STARTER))

    // 核心模块
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))

    // 请求追踪模块
    compileOnly(project(Modules.TRACE.SPRING_BOOT_STARTER))

    // Web模块
    compileOnly(project(Modules.WEB.SPRING_BOOT_STARTER))

    // Spring Boot Starters (Compile Only)
    compileOnly(libs.springBootStarter.aop)
    compileOnly(libs.springBootStarter.validation)

    // Redisson Starter (Compile Only)
    compileOnly(libs.redisson)

    // Apache Commons (Compile Only)
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    compileOnly(libs.commons.lang3)
}