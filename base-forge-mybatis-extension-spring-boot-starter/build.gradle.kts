dependencies {
    val libs = rootProject.libs

    // MyBatis Plus Spring Boot 3 Starter (API scope)
    api(libs.mybatis.plus.spring.boot3.starter)

    // SpringDoc OpenAPI WebMVC UI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}