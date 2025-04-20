dependencies {
    val libs = rootProject.libs

    // Spring Boot AOP Starter
    implementation(libs.springBootStarter.aop)

    // SpringDoc OpenAPI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // 国际化模块
    implementation(project(Modules.I18N.SPRING_BOOT_STARTER))

    // 工具模块
    implementation(project(Modules.UTIL.SPRING_BOOT_STARTER))

    // 日志模块
    implementation(project(Modules.LOG.SPRING_BOOT_STARTER))
}