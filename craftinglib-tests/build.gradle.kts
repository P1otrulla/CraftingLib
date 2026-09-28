// Integration tests on MockBukkit. Not published. MockBukkit needs Java 17.
plugins {
    java
    id("craftinglib-junit")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    testImplementation(project(":craftinglib-bukkit-okaeri-serdes"))
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:${Versions.MOCKBUKKIT}")
    testImplementation("eu.okaeri:okaeri-configs-yaml-bukkit:${Versions.OKAERI_CONFIGS}")
    testImplementation("eu.okaeri:okaeri-configs-serdes-bukkit:${Versions.OKAERI_CONFIGS}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JAVA_TOOLCHAIN))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(Versions.TESTS_JAVA_RELEASE)
}

// MockBukkit 3.x proxies plugins with ByteBuddy, which injects classes via ClassLoader#defineClass.
// On newer JDKs that needs java.lang opened (and experimental mode for JDKs ByteBuddy does not know yet).
tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    systemProperty("net.bytebuddy.experimental", "true")
}
