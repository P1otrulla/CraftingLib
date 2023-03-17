package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.recipe.RecipeAccessor;
import dev.piotrulla.craftinglib.recipe.RecipeDetector;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class CraftingLib {

    private final CraftingRegistry craftingRegistry;
    private final CraftingManager craftingManager;
    private final RecipeAccessor recipeAccessor;
    private final Plugin plugin;

    public CraftingLib(Plugin plugin) {
        this.plugin = plugin;

        Server server = plugin.getServer();

        this.recipeAccessor = RecipeDetector.detectRecipe(server);

        this.craftingRegistry = new CraftingRegistry(this.recipeAccessor, server);
        this.craftingManager = new CraftingManager(this.craftingRegistry);

        server.getPluginManager().registerEvents(new CraftingController(this.craftingManager), plugin);
    }

    public CraftingRegistry getCraftingRegistry() {
        return this.craftingRegistry;
    }

    public CraftingManager getCraftingManager() {
        return this.craftingManager;
    }

    public RecipeAccessor getRecipe() {
        return this.recipeAccessor;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
