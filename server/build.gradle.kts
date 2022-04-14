plugins {
    kotlin("jvm") version "1.6.0-M1"
    id("io.ratpack.ratpack-java")
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
    implementation(project(":shared-web"))
    implementation(project(":game-history"))
    implementation(project(":bots"))
    implementation(kotlin("stdlib"))
    implementation("me.kcybulski.nexum:event-store:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.github.microutils:kotlin-logging:1.12.5")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.github.javafaker:javafaker:1.0.2")
}

application {
    mainClass.set("me.kcybulski.bricks.server.StartKt")
}
