val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("io.ktor.plugin") version "2.2.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
}

group = "com"
version = "0.0.1"
application {
    mainClass.set("com.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-locations-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-webjars-jvm:$ktor_version")
    implementation("org.webjars:jquery:3.6.1")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core-jvm:2.1.3")
    implementation ("io.ktor:ktor-server-partial-content:$ktor_version")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    implementation ("io.ktor:ktor-server-compression:$ktor_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation ("org.xerial:sqlite-jdbc:3.39.3.0")
    implementation("redis.clients:jedis:4.3.1")

    // https://mvnrepository.com/artifact/io.ktor/ktor-client-core-jvm
    implementation("io.ktor:ktor-client-core-jvm:2.1.3")
// https://mvnrepository.com/artifact/io.ktor/ktor-client-serialization-jvm
    implementation("io.ktor:ktor-client-serialization-jvm:2.1.3")
// https://mvnrepository.com/artifact/io.ktor/ktor-client-cio-jvm
    implementation("io.ktor:ktor-client-cio-jvm:2.1.3")
// https://mvnrepository.com/artifact/io.ktor/ktor-client-content-negotiation-jvm
    implementation("io.ktor:ktor-client-content-negotiation-jvm:2.1.3")
// https://mvnrepository.com/artifact/io.ktor/ktor-client-json-jvm
    implementation("io.ktor:ktor-client-json-jvm:2.1.3")

// https://mvnrepository.com/artifact/io.ktor/ktor-client-gson-jvm
    implementation("io.ktor:ktor-client-gson-jvm:2.1.3")
    implementation("io.ktor:ktor-client-encoding:2.1.3")

}