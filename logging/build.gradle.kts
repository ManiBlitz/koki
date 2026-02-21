plugins {
    kotlin("jvm")
}

group = "com.smallee"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.slf4j)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
