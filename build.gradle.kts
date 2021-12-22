import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

repositories { mavenCentral() }

allprojects {
    apply(plugin = "kotlin")
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            version = "17"
            jvmTarget = "17"
            languageVersion = "1.6"
        }
    }
}
