import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-M1"
}

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "kotlin")
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.5"
            useIR = true
        }
    }
}
