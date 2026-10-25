// Maven publication to repo.eternalcode.pl (./gradlew publish) and ~/.m2 (./gradlew publishToMavenLocal).
// Sources and javadoc jars come from craftinglib-java.
plugins {
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/P1otrulla/CraftingLib")

                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "eternalcodeReleases"
            url = uri("https://repo.eternalcode.pl/releases")
            credentials {
                username = System.getenv("ETERNAL_CODE_MAVEN_USERNAME")
                password = System.getenv("ETERNAL_CODE_MAVEN_PASSWORD")
            }
        }
    }
}
