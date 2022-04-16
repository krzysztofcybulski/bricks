plugins {
    kotlin("jvm") version "1.6.0-M1"
    `java-library`
    `maven-publish`
    signing
}

group = "me.kcybulski.bricks"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":game"))
    api(project(":test"))
    implementation(project(":events"))
    implementation(project(":shared-web"))
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-websockets:2.0.0")
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-jackson:2.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("io.github.microutils:kotlin-logging:1.12.5")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha5")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "bricks-client"
            version = "2.0"
            pom {
                name.set("bricks-client")
                url.set("https://github.com/krzysztofcybulski/bricks")
                description.set("Bricks game client")
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
