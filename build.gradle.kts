import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    // id("com.google.cloud.tools.jib") version "3.4.1"
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.flyway)
    jacoco
    java
}

group = "org.kerminator"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven(url = "https://repo.spring.io/milestone")
}

sonarqube {
    properties {
        property("sonar.projectKey", "spring-hello")
        property("sonar.organization", "jahpola")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.tests", "src/test/java")
    }
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("public.ecr.aws/kerminator/spring-${project.name}")
    createdDate.set("now")
    environment.put("BP_JVM_VERSION", "25")
    // environment.put("BP_JVM_JLINK_ENABLED", "true")
    // environment.put("BP_JVM_CDS_ENABLED", "true")
    // environment.put("BP_SPRING_AOT_ENABLED", "true")
}

dependencies {
    implementation(libs.spring.boot)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.spring.cloud.starter.kubernetes.fabric8.all)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.caffeine)
    // implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    // implementation("io.micrometer:micrometer-tracing-bridge-otel")
    // implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)
    // runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.starter.actuator.test)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.junit.platform.launcher)

    developmentOnly(libs.spring.boot.docker.compose)
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.cloud.dependencies.get().toString())
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    maxParallelForks = 4
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}
