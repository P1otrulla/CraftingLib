plugins {
    `java-library`
}

group = "dev.piotrulla.craftinglib"
version = "4.0.0"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(project(":craftinglib-core"))

    api("org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS}")

    compileOnly("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")
}

tasks.test {
    useJUnitPlatform()
}