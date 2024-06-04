import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.0.0"

	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("plugin.spring") version "2.0.0"

	id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
}

group = "pro.azhidkov.case_studies"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.postgresql:postgresql")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.kotest:kotest-property:5.9.0")
	testImplementation("io.kotest:kotest-assertions-core:5.9.0")

	testImplementation("io.rest-assured:rest-assured:5.4.0")
	testImplementation("io.rest-assured:kotlin-extensions:5.4.0")
	testImplementation("io.rest-assured:json-schema-validator:5.4.0")

	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("net.datafaker:datafaker:2.2.2")

	runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// см. https://github.com/Kotlin/dokka/issues/3472
	configurations.matching { it.name.startsWith("dokka") }.configureEach {
		resolutionStrategy.eachDependency {
			if (requested.group.startsWith("com.fasterxml.jackson")) {
				useVersion("2.15.3")
			}
		}
	}
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

idea {
	module {
		settings {
			packagePrefix["src/main/kotlin"] = "pro.azhidkov"
			packagePrefix["src/test/kotlin"] = "pro.azhidkov.mariotte"
		}
	}
}