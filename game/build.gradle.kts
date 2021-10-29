plugins {
    kotlin("jvm") version "1.6.0-M1"
}

group = "me.kcybulski.bricks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":events"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("io.arrow-kt:arrow-core:1.0.0")
    implementation("com.github.javafaker:javafaker:1.0.2")

    testImplementation(project(":test"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.0.M3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.0.M3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
