package dev.piotrulla.craftinglib.recipe;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public final class RecipeDetector {

    private RecipeDetector() {
    }

    public static RecipeAccessor detectRecipe(Plugin plugin) {
        String version = plugin.getServer().getClass().getName().split("\\.")[3];

        switch (version) {
            case "v1_8_R1":
            case "v1_8_R2":
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1": {
                return new OldRecipe();
            }

            case "v1_12_R1": {
                return new NewRecipe(plugin);
            }

            default: {
                return new NewerRecipe(plugin);
            }
        }
    }
}
