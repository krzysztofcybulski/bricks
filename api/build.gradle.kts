plugins {
    kotlin("jvm") version "1.6.20"
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
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "bricks-api"
            version = "2.2"
            pom {
                name.set("bricks-api")
                url.set("https://github.com/krzysztofcybulski/bricks")
                description.set("Bricks API")
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
