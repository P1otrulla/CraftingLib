package net.osnixer.craftinglib;

import net.osnixer.craftinglib.recipe.NewRecipe;
import net.osnixer.craftinglib.recipe.NewerRecipe;
import net.osnixer.craftinglib.recipe.OldRecipe;
import net.osnixer.craftinglib.recipe.RecipeNMS;
import org.bukkit.plugin.Plugin;

public class CraftingLib {

    private final CraftingManager craftingManager;
    private static CraftingLib instance;
    private final RecipeNMS recipeNMS;
    private final Plugin plugin;

    public CraftingLib(Plugin plugin){
        instance = this;

        this.plugin = plugin;

        this.craftingManager = new CraftingManager();

        plugin.getServer().getPluginManager().registerEvents(new CraftingListeners(this.craftingManager), plugin);

        this.recipeNMS = this.detectVersion(plugin);

    }

    private RecipeNMS detectVersion(Plugin plugin) {
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
                return new NewRecipe();
            }

            default: {
                return new NewerRecipe();
            }
        }
    }

    public CraftingManager getCraftingManager() {
        return this.craftingManager;
    }

    public static CraftingLib getInstance() {
        return instance;
    }

    public RecipeNMS getRecipeNMS() {
        return this.recipeNMS;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
