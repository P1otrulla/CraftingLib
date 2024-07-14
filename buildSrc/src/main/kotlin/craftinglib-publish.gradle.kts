plugins {
    `java-library`
    `maven-publish`
}

group = "dev.piotrulla"
version = "3.1.0"

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(project.components["java"])
            artifactId = "craftinglib"
        }
    }
    repositories {
        mavenLocal()
        maven(
            name = "eternalcode",
            url = "https://repo.eternalcode.pl",
            username = "ETERNAL_CODE_MAVEN_USERNAME",
            password = "ETERNAL_CODE_MAVEN_PASSWORD",
        )
    }
}

fun RepositoryHandler.maven(name: String, url: String, username: String, password: String) {
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")
    this.maven {
        this.name =
            if (isSnapshot) "${name}Snapshots"
            else "${name}Releases"
        this.url =
            if (isSnapshot) uri("$url/snapshots")
            else uri("$url/releases")
        this.credentials {
            this.username = System.getenv(username)
            this.password = System.getenv(password)
        }
    }
}