import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    kotlin("jvm") version "1.3.61"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

group = "com.michaelstrasser"
version = "0.2.5"

repositories {
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
}

dependencies {
    val kotlinVersion = "1.3.61"
    val ktorVersion = "1.2.6"
    val junit5Version = "5.5.2"
    val spekVersion = "2.0.9"
    val assertkVersion = "0.20"

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = kotlinVersion)

    implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)

    testImplementation(group = "io.ktor", name = "ktor-client-mock", version = ktorVersion)
    testImplementation(group = "io.ktor", name = "ktor-client-mock-jvm", version = ktorVersion)
    testImplementation(group = "io.ktor", name = "ktor-server-tests", version = ktorVersion)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junit5Version)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junit5Version)

    testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = assertkVersion)

    // Override the version in Spek.
    testImplementation(kotlin(module = "reflect", version = kotlinVersion))
    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = spekVersion)
    testRuntimeOnly(group = "org.spekframework.spek2", name = "spek-runner-junit5", version = spekVersion)
}

tasks.test {
    useJUnitPlatform()
    dependsOn("cleanTest")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlin.Experimental")
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
        }
    }
    repositories {
        maven {
            name = "buildDirRepo"
            url = uri("file://${buildDir}/repo")
        }
    }
}
