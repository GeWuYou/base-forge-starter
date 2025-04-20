dependencies {
    val libs = rootProject.libs

    // Spring Data Redis Starter
    implementation(libs.springBootStarter.data.redis)

    // Base Forge Core Modules (Compile Only)
    // 核心模块 (compileOnly)
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))

    // 国际化模块 (compileOnly)
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))

    // JSON处理模块 (compileOnly)
    compileOnly(project(Modules.JSON.SPRING_BOOT_STARTER))
}