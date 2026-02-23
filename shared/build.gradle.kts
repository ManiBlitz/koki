plugins {
    kotlin("jvm")
}

group = "com.smallee"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.opentelemetryApi)

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
    environment("OBFUSCATION_ENABLED", "true")
}

kotlin {
    jvmToolchain(21)
}
