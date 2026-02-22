import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.sonarqube)
}

sonar {
    properties {
        property("sonar.projectKey", "koki")
        property("sonar.projectName", "Koki")
        // Aggregate JaCoCo XML reports from every submodule for coverage analysis
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            subprojects.map {
                "${it.layout.buildDirectory.get().asFile}/reports/jacoco/test/jacocoTestReport.xml"
            }.joinToString(","),
        )
    }
}

subprojects {
    apply(plugin = "jacoco")
    apply(plugin = "com.diffplug.spotless")

    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }

    tasks.withType<Test>().configureEach {
        finalizedBy(tasks.withType<JacocoReport>())
    }

    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            // Target all .kt files in the project, excluding those in the build directory
            target("**/*.kt")
            targetExclude("${layout.buildDirectory.get().asFile}/**/*.kt")

            ktfmt().googleStyle()

            // Optional: Add a license header to the top of your files
            // licenseHeaderFile(rootProject.file("spotless/copyright.kt"))

            // Optional: Configure specific ktlint rules via .editorconfig
            // .editorconfig is the standard way to configure ktlint rules
            // You can specify an external file:
            // setEditorConfigPath("${project.rootDir}/spotless/.editorconfig")
        }

        // Optional: Configure formatting for Kotlin Gradle script files
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
        }
    }
}