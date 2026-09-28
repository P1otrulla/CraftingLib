// okaeri-configs core + Bukkit serdes (ItemStack etc.).
plugins {
    id("craftinglib-java")
}

repositories {
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    api("eu.okaeri:okaeri-configs-core:${Versions.OKAERI_CONFIGS}")
    api("eu.okaeri:okaeri-configs-serdes-bukkit:${Versions.OKAERI_CONFIGS}")
}
