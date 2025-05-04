import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

fun Project.configureMavenPublication(
    artifactId: String,
    descriptionText: String,
    componentName: String = "java",
    publicationName: String = "mavenJava"
) {
    val baseUrl         = property("pomBaseUrl")        as String
    val branch          = findProperty("pomDefaultBranch")          as? String ?: "main"
    val licenseName     = property("pomLicenseName")    as String
    val developerId     = property("pomDeveloperId")    as String
    val developerName   = property("pomDeveloperName")  as String
    val developerEmail  = property("pomDeveloperEmail") as String

    val licenseUrl      = "$baseUrl/blob/$branch/LICENSE"
    val repoHostAndPath = baseUrl.removePrefix("https://")
    val repoPath        = repoHostAndPath.removeSuffix(".git")
    val scmConnection   = "scm:git:git://$repoPath.git"
    val scmDevConn      = "scm:git:ssh://$repoPath.git"

    extensions.configure<PublishingExtension> {
        publications.create<MavenPublication>(publicationName) {
            from(project.components.getByName(componentName))
            this.artifactId = artifactId

            pom {
                name.set(artifactId)
                description.set(descriptionText)
                url.set(baseUrl)

                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }

                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                        email.set(developerEmail)
                    }
                }

                scm {
                    connection.set(scmConnection)
                    developerConnection.set(scmDevConn)
                    url.set(baseUrl)
                }
            }
        }
    }
}

fun Project.configureSigning(
    publicationName: String = "mavenJava",
    signingKeyProp: String = "signing.key",
    signingPwdProp: String = "signing.password"
) {
    pluginManager.apply("signing")

    val key = findProperty(signingKeyProp) as? String
        ?: System.getenv("SIGNING_KEY")
    val pwd = findProperty(signingPwdProp) as? String
        ?: System.getenv("SIGNING_PASSWORD")

    extensions.configure<SigningExtension> {
        if (!key.isNullOrBlank() && !pwd.isNullOrBlank()) {
            useInMemoryPgpKeys(key, pwd)

            val publishingExt = project.extensions.getByType<PublishingExtension>()
            val publication: MavenPublication = publishingExt
                .publications
                .withType<MavenPublication>()
                .getByName(publicationName)

            sign(publication)
        } else {
            isRequired = false
        }
    }
}


