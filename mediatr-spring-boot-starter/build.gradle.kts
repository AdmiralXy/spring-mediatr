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
    api(project(":mediatr-core"))
    api(project(":mediatr-spring-boot-autoconfigure"))

    compileOnly("org.springframework.boot:spring-boot-starter")
}

configureMavenPublication(
    artifactId = "spring-boot-starter-mediatr",
    descriptionText = "Spring Boot starter for Mediator pattern implementation"
)
configureSigning()
