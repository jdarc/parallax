import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

repositories { mavenCentral() }

allprojects {
    apply(plugin = "kotlin")
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            version = 16
            jvmTarget = "16"
            languageVersion = "1.5"
        }
    }
}
