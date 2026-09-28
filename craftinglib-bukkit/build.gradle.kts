plugins {
    id("craftinglib-paper")
    id("craftinglib-publish")
}

description = "Bukkit/Paper implementation of CraftingLib (1.8+)"

dependencies {
    api(project(":craftinglib-core"))
}
