/*
 * Copyright 2019 Stamina Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.kotlin

    jacoco
    `maven-publish`
    id("org.jetbrains.dokka") version Versions.dokka
}

group = "pw.stamina"
version = "1.0.0-SNAPSHOT"
description = "A simple yet powerful pubsub system for Java and Kotlin."

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Dependencies.spekDslJvm)
    testRuntimeOnly(Dependencies.spekRunnerJUnit5)

    testImplementation(Dependencies.kluent)
    testImplementation(Dependencies.mockitoKotlin)

    testRuntimeOnly(kotlin("reflect"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    check {
        dependsOn(jacocoTestReport)
    }

    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}

jacoco {
    toolVersion = Versions.jacoco
}

val dokka by tasks.existing(DokkaTask::class) {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(dokka)
}

publishing {
    publications {
        create<MavenPublication>("pubsub4k") {
            from(components["java"])

            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                val projectUrl = "https://github.com/staminadevelopment/pubsub4k"

                name.set(project.name)
                description.set(project.description)
                url.set(projectUrl)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("feature")
                        name.set("N3xuz")
                        email.set("n3xuz@stamina.pw")
                    }
                }

                scm {
                    url.set(projectUrl)
                }
            }
        }
    }
}