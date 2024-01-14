val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val h2_version: String by project
val ktorm_version :String by project
plugins {
	application
	kotlin("jvm") version "1.9.20"
	id("io.ktor.plugin") version "2.3.7"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

group = "com.wusui"
version = "0.0.1"
application {
	mainClass.set("com.wusui.ApplicationKt")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
	maven("https://maven-central-asia.storage-download.googleapis.com/maven2/")
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}
dependencies {
	implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-locations-jvm:$ktor_version")
	implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-webjars-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
	implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
	implementation("ch.qos.logback:logback-classic:$logback_version")
	implementation("io.ktor:ktor-client-core-jvm:2.1.3")
	implementation("io.ktor:ktor-server-partial-content:$ktor_version")
	implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
	testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-compression:$ktor_version")
	implementation("io.ktor:ktor-server-config-yaml-jvm:2.3.7")

	testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
	implementation("redis.clients:jedis:5.0.2")

	implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")

	//Database
	implementation("org.ktorm:ktorm-core:${ktorm_version}")
	implementation("org.ktorm:ktorm-jackson:$ktorm_version")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")

	implementation("com.alibaba:druid:1.2.18")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.ktorm:ktorm-support-mysql:${ktorm_version}")
}