plugins {
    kotlin("jvm") version "1.6.0-M1"
}

group = "me.kcybulski.bricks"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":api"))
    implementation(project(":tournament"))
    implementation(project(":events"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}