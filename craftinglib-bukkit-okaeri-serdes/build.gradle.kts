plugins {
    `java-library`
}

group = "dev.piotrulla.craftinglib"
version = "4.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(project(":craftinglib-bukkit"))

    compileOnly("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")

    compileOnly("eu.okaeri:okaeri-configs-core:5.0.9")
    compileOnly("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.9")
}

tasks.test {
    useJUnitPlatform()
}