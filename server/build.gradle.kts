plugins {
    kotlin("jvm") version "1.6.0-M1"
}

group = "me.kcybulski.bricks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":events"))
    implementation(project(":tournament"))
    implementation(project(":test"))
    implementation(kotlin("stdlib"))
}
