
dependencies {
    // 核心模块 (compileOnly)
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))

    // 国际化模块 (compileOnly)
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))
    compileOnly(libs.springBootStarter.security)
}
