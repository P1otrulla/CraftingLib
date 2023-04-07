plugins {
    `java-library`
    `maven-publish`

    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "dev.piotrulla"
version = "2.0.0"
val artifactId = "craftinglib"

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven { url = uri("https://papermc.io/repo/repository/maven-public/")}
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "$group"
            artifactId = artifactId
            version = "${project.version}"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "EternalCodeRepo"
            url = uri("https://repo.eternalcode.pl/releases")

            // add repoNameUsername and repoNamePassword to gradle.properties file in .gradle
            credentials(PasswordCredentials::class)
        }
    }
}


tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("CraftingLib v${project.version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "org/checkerframework/**",
        "META-INF/**",
        "javax/**"
    )

    mergeServiceFiles()
    minimize()
}