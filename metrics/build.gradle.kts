plugins {
    kotlin("jvm")
}

group = "com.smallee"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.opentelemetryApi)

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
