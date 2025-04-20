dependencies {
    val libs = rootProject.libs

    // Base Forge Modules (Compile Only)
    // 国际化模块 (compileOnly)
    compileOnly(project(Modules.I18N.SPRING_BOOT_STARTER))

    // 工具模块 (compileOnly)
    compileOnly(project(Modules.UTIL.SPRING_BOOT_STARTER))

    // 请求追踪模块 (compileOnly)
    compileOnly(project(Modules.TRACE.SPRING_BOOT_STARTER))
    // Spring Data JPA (Compile Only)
    compileOnly(libs.springBootStarter.data.jpa)

    // SpringDoc OpenAPI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

}