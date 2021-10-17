import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.31"
}

group = "me.aleks"
version = "1.0-SNAPSHOT"

val ktorVersion = "1.6.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor", "ktor-client-serialization", ktorVersion)
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("com.xebialabs.restito:restito:0.9.4")
    testImplementation("io.mockk:mockk:1.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}