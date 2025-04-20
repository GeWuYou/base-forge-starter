dependencies {
    val libs = rootProject.libs

    // Base Forge Core Starter
    implementation(project(Modules.CORE.SPRING_BOOT_STARTER))

    // Reactor Core (Compile Only)
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-core
    compileOnly(libs.reactor.core)

    // Spring Cloud OpenFeign (Compile Only)
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    compileOnly(libs.springCloudStarter.openfeign)
}