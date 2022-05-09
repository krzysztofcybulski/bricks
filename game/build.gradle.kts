plugins {
    kotlin("jvm") version "1.6.0-M1"
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
    api(project(":api"))
    implementation(project(":events"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.arrow-kt:arrow-core:1.0.0")
    implementation("com.github.javafaker:javafaker:1.0.2")

    testImplementation(project(":test"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.0.M3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.0.M3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "bricks-game"
            version = "2.2"
            pom {
                name.set("bricks-game")
                url.set("https://github.com/krzysztofcybulski/bricks")
                description.set("Bricks game")
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
