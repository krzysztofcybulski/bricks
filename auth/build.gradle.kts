plugins {
    kotlin("jvm") version "1.6.20"
}

group = "me.kcybulski.bricks"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":events"))
}
