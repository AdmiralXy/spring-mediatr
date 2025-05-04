dependencies {
    api(project(":mediatr-api"))

    compileOnly(libs.slf4j.api)
    testRuntimeOnly(libs.slf4j.simple)
}

configureMavenPublication(
    artifactId = "mediatr-core",
    descriptionText = "Spring Boot Mediatr core"
)
configureSigning()
