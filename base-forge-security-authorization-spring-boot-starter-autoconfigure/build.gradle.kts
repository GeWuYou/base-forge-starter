dependencies {
    val libs = rootProject.libs
    // webflux
    implementation(libs.springBootStarter.webflux)

    // JWT
    implementation(libs.bundles.jjwtAll)
    // ===== Base Forge Implementation Modules =====
    implementation(project(Modules.WEB.SPRING_BOOT_STARTER))
    implementation(project(Modules.JSON.SPRING_BOOT_STARTER))
    implementation(project(Modules.REDIS.SPRING_BOOT_STARTER))
    implementation(project(Modules.SECURITY.Authentication.SPRING_BOOT_STARTER))

    // ===== Base Forge API Modules =====
    api(project(Modules.TRACE.SPRING_BOOT_STARTER))
    api(project(Modules.JWT.SPRING_BOOT_STARTER))
    api(libs.springBootStarter.security)

    // ===== Base Forge Compile Only Modules =====
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))
    compileOnly(project(Modules.SECURITY.AUTHORIZATION.SPRING_BOOT_STARTER))
}