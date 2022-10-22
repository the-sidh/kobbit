import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("maven-publish")
    id("signing")
}

group = "io.github.the_sidh"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("com.github.the_sidh:kobbit:1.0.0")
            }
        }
    }
}

val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")
val javadocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        repositories {
            maven {
                name="oss"
                val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom { /* ... */ }
            }
        }
    }
}



dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation( "io.insert-koin:koin-core:3.2.2")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}
