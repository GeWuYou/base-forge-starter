dependencies {
    val libs = rootProject.libs

    // Spring Cloud OpenFeign
    implementation(libs.springCloudStarter.openfeign)

    // Base Forge project dependencies
    implementation(project(Modules.JSON.SPRING_BOOT_STARTER))
    implementation(project(Modules.WEB.SPRING_BOOT_STARTER))
}