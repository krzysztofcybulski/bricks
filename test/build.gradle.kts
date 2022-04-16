plugins {
    kotlin("jvm") version "1.6.0-RC"
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
    api("io.kotest:kotest-runner-junit5-jvm:5.0.0.M3")
    api("io.kotest:kotest-assertions-core-jvm:5.0.0.M3")
    implementation(kotlin("stdlib"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "bricks-test"
            version = "2.0"
            pom {
                name.set("bricks-test")
                url.set("https://github.com/krzysztofcybulski/bricks")
                description.set("Bricks test utils")
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
