plugins {
    kotlin("jvm") version "1.6.0-M1"
}

group = "me.kcybulski.bricks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":game"))
    implementation(project(":events"))
    implementation(project(":shared-web"))
    implementation(project(":test"))
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-websockets:1.6.4")
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-cio:1.6.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("io.github.microutils:kotlin-logging:1.12.5")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha5")
}
