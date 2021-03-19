import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Engine"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val fatJar = task("fatJar", type = Jar::class) {
    manifest { attributes["Main-Class"] = "demo.runaround.Program" }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

application {
    mainClass.set("demo.runaround.Program")
}
