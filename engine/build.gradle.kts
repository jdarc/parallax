repositories { mavenCentral() }

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.17.2")
}

tasks.test { useJUnitPlatform() }
