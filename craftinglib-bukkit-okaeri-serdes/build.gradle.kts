plugins {
    id("craftinglib-paper")
    id("craftinglib-okaeri")
    id("craftinglib-publish")
}

description = "okaeri-configs serializers for CraftingLib recipes"

dependencies {
    api(project(":craftinglib-bukkit"))
}
