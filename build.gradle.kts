group = "me.kcybulski.bricks"
version = "2.0"

repositories {
    mavenCentral()
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.ratpack:ratpack-gradle:1.9.0")
    }
}

nexusPublishing {
    packageGroup.set("me.kcybulski.bricks")
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
