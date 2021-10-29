plugins {
    kotlin("jvm") version "1.6.0-RC"
}

group = "me.kcybulski.bricks.web"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
}
