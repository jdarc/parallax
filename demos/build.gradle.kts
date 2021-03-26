import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-M1"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":engine"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "15"
        languageVersion = "1.5"
        useIR = true
    }
}

application {
    mainClass.set("demo.inspector.ProgramKt")
}
