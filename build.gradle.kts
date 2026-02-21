plugins {
    id("com.diffplug.spotless") version "8.2.1"
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            // Target all .kt files in the project, excluding those in the build directory
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")

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