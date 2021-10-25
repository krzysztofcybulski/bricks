plugins {
    kotlin("jvm") version "1.6.0-RC"
}

group = "me.kcybulski.bricks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":game"))
    api("io.kotest:kotest-runner-junit5-jvm:5.0.0.M3")
    api("io.kotest:kotest-assertions-core-jvm:5.0.0.M3")
    implementation(kotlin("stdlib"))
}
