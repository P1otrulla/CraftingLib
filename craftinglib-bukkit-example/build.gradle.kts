import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

// Showcase plugin with every recipe type, used for the README video. Not published.
// ./gradlew :craftinglib-bukkit-example:runServer
plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("de.eldoria.plugin-yml.bukkit") version "0.9.0"
}

description = "CraftingLib showcase plugin"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JAVA_TOOLCHAIN))
    disableAutoTargetJvm()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${Versions.PAPER_API}")

    implementation(project(":craftinglib-bukkit-okaeri-serdes"))
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:${Versions.OKAERI_CONFIGS}")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(Versions.JAVA_TOOLCHAIN)
}

tasks.shadowJar {
    archiveFileName.set("CraftingLibShowcase.jar")

    val libs = "dev.piotrulla.craftingshowcase.libs"
    relocate("dev.piotrulla.craftinglib", "$libs.craftinglib")
    relocate("eu.okaeri", "$libs.okaeri")
}

tasks.runServer {
    minecraftVersion(Versions.SHOWCASE_MINECRAFT)
    // Minecraft 26.x needs Java 25 to run the server (the plugin itself stays Java 21 bytecode)
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(Versions.SHOWCASE_SERVER_JAVA))
    })
    jvmArgs(
        // Accepts the Minecraft EULA (https://aka.ms/MinecraftEULA) for this local test server.
        "-Dcom.mojang.eula.agree=true",
        // Skips Paper's Java version check on startup.
        "-DPaper.IgnoreJavaVersion=true"
    )
}

bukkit {
    name = "CraftingLibShowcase"
    main = "dev.piotrulla.craftingshowcase.CraftingShowcasePlugin"
    apiVersion = "1.21"
    authors = listOf("PIOTRULLA")

    commands {
        register("showcase") {
            description = "Opens the CraftingLib showcase menu or gives a kit"
            usage = "/showcase [kit|all]"
            permission = "craftingshowcase.command"
        }
    }

    permissions {
        register("craftingshowcase.command") {
            description = "Allows using /showcase"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
