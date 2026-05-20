import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage


ext {
    set("springBootVersion", "4.0.6")
    set("dependencyManagementVersion", "1.1.7")
    set("sonarqubeVersion", "7.3.0.8198")
    set("flywayVersion", "12.6.1")
    set("springCloudVersion", "2025.1.1")
    set("springdocOpenapiVersion", "3.0.3")
    set("caffeineVersion", "3.2.4")
}

plugins {
    id("org.springframework.boot") version extra["springBootVersion"] as String
    id("io.spring.dependency-management") version extra["dependencyManagementVersion"] as String
    // id("com.google.cloud.tools.jib") version "3.4.1"
    id("org.sonarqube") version extra["sonarqubeVersion"] as String
    id("org.flywaydb.flyway") version extra["flywayVersion"] as String
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


// springCloudVersion is now set in ext above

dependencies {
    implementation("org.springframework.boot:spring-boot:${extra["springBootVersion"]}")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${extra["springdocOpenapiVersion"]}")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-fabric8-all")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:${extra["caffeineVersion"]}")
    // implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    // implementation("io.micrometer:micrometer-tracing-bridge-otel")
    // implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")
    // runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${extra["springCloudVersion"]}")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    // maxParallelForks = 4
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}
