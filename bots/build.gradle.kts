plugins {
    kotlin("jvm") version "1.6.20"
}

group = "me.kcybulski.bricks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":game"))
    implementation("com.github.javafaker:javafaker:1.0.2")
}
