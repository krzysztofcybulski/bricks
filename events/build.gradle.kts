plugins {
    kotlin("jvm") version "1.6.0-RC"
    `java-library`
    `maven-publish`
    signing
}

group = "me.kcybulski.bricks"
version = "2.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    api("me.kcybulski.nexum:event-store:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha5")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "bricks-events"
            version = "2.2"
            pom {
                name.set("bricks-events")
                url.set("https://github.com/krzysztofcybulski/bricks")
                description.set("Bricks events")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("krzysztofcybulski")
                        name.set("Krzysztof Cybulski")
                    }
                }
                scm {
                    url.set("https://github.com/krzysztofcybulski/bricks")
                }
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    sign(publishing.publications["mavenJava"])
}
