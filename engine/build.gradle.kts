import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-M1"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.17.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "15"
        languageVersion = "1.5"
        useIR = true
    }
}

tasks.test {
    useJUnitPlatform()
}
