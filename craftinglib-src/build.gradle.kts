plugins {
    `craftinglib-java`
    `craftinglib-publish`
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")

    api("org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
