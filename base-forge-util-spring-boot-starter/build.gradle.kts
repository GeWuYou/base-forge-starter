dependencies {
    val libs = rootProject.libs

    // Base Forge Modules (Compile Only)
    // 核心模块 (compileOnly)
    compileOnly(project(Modules.CORE.SPRING_BOOT_STARTER))

    // 国际化模块 (compileOnly)
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))

    // JSON处理模块 (compileOnly)
    compileOnly(project(Modules.JSON.SPRING_BOOT_STARTER))

    // SpringDoc OpenAPI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // UserAgent Utilities
    // https://mvnrepository.com/artifact/eu.bitwalker/UserAgentUtils
    implementation(libs.userAgentUtils)

    // Jackson Bundle
    implementation(libs.bundles.jacksonAll)
}