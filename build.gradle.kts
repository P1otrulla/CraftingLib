plugins {
    `java-library`
    `maven-publish`
}

group = "dev.piotrulla"
version = "3.0.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
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