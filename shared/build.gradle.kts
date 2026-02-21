plugins {
    kotlin("jvm")
}

group = "com.smallee"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    api(libs.opentelemetryApi)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
