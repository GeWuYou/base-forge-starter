dependencies {
    val libs = rootProject.libs

    // Security
    api(libs.springBootStarter.security)
    // JWT 认证模块 (API暴露)
    api(project(Modules.JWT.SPRING_BOOT_STARTER))

    // ===== 基础核心模块 (仅编译期) =====
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))
    compileOnly(project(Modules.WEB.SPRING_BOOT_STARTER))
    compileOnly(project(Modules.SECURITY.Authentication.SPRING_BOOT_STARTER))

    // ===== 实现模块 (运行时依赖) =====
    implementation(project(Modules.REDIS.SPRING_BOOT_STARTER))
    implementation(project(Modules.JSON.SPRING_BOOT_STARTER))

    // Commons Lang3
    implementation(libs.commons.lang3)

    // JWT Bundle
    implementation(libs.bundles.jjwtAll)
}