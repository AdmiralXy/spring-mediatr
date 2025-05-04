plugins {
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.plugin.get()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.dependencies.get()}")
    }
}

dependencies {
    api(project(":mediatr-api"))
    implementation(project(":mediatr-core"))

    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configureMavenPublication(
    artifactId = "mediatr-spring-boot-autoconfigure",
    descriptionText = "Spring Boot Mediatr auto-configuration"
)
configureSigning()
