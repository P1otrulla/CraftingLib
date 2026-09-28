plugins {
    // downloads the JDK toolchain automatically when it is not installed
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "craftinglib"

include(
    "craftinglib-core",
    "craftinglib-bukkit",
    "craftinglib-bukkit-okaeri-serdes",
    "craftinglib-tests"
)
