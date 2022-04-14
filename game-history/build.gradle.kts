plugins {
    kotlin("jvm") version "1.6.0-RC2"
}

group = "me.kcybulski.bricks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":game"))
    implementation(project(":tournament"))
    implementation(project(":events"))
}
