plugins {
    `java-library`
    checkstyle
    jacoco
    id("jacoco-report-aggregation")

    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME"))
            password.set(findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD"))
        }
    }
}

val secretKey: String? = findProperty("signing.key")      as String? ?: System.getenv("SIGNING_KEY")
val secretPwd: String? = findProperty("signing.password") as String? ?: System.getenv("SIGNING_PASSWORD")

if (secretKey.isNullOrBlank()) {
    logger.warn("PGP key or password is not configured – signing will be skipped")
}

ext["inMemoryKey"] = secretKey
ext["inMemoryPwd"] = secretPwd

tasks.register<JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { it.tasks.named("test") })

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val classDirs = files(subprojects.map {
        fileTree("${it.layout.buildDirectory.get()}/classes/java/main") {
            exclude("**/generated/**")
        }
    })

    val sourceDirs = files(subprojects.map {
        file("${it.projectDir}/src/main/java")
    })

    val executionData = files(subprojects.map {
        file("${it.layout.buildDirectory.get()}/jacoco/test.exec")
    })

    classDirectories.setFrom(classDirs)
    sourceDirectories.setFrom(sourceDirs)
    this.executionData.setFrom(executionData)
}

tasks.named("check") {
    dependsOn("checkstyleMain", "checkstyleTest", "jacocoRootReport")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(17)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    jacoco {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        enabled = false
    }

    tasks.withType<Sign>().configureEach {
        group = "signing"
    }

    dependencies {
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)

        testImplementation(rootProject.libs.junit.jupiter)
        testRuntimeOnly(rootProject.libs.junit.platform)

        testImplementation(rootProject.libs.assertj)
        testImplementation(rootProject.libs.mockito)
    }
}
