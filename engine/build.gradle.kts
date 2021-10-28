repositories { mavenCentral() }

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.assertj:assertj-core:3.21.0")
}

tasks.test { useJUnitPlatform() }
