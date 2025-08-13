package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.registry.CraftingRecipeRegistry;
import dev.piotrulla.craftinglib.version.VersionDetector;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit implementation of CraftingLib.
 */
public class BukkitCraftingLib implements CraftingLib<ItemStack> {

    private final BukkitCraftingRecipeRegistry registry;
    private final BukkitCraftingRecipeManager manager;
    private final Plugin plugin;

    private boolean initialized = false;

    public BukkitCraftingLib(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.registry = new BukkitCraftingRecipeRegistry(plugin.getServer(), VersionDetector.detectVersion(plugin));
        this.manager = new BukkitCraftingRecipeManager(this.registry);
    }

    @Override
    public void initialize() {
        if (this.initialized) {
            return;
        }

        // Register action listeners
        this.plugin.getServer().getPluginManager()
                .registerEvents(new CraftingEventListener(this.manager), this.plugin);

        this.initialized = true;
        this.plugin.getLogger().info("BukkitCraftingLib initialized successfully");
    }

    @Override
    @NotNull
    public CraftingRecipeManager<ItemStack> getRecipeManager() {
        return this.manager;
    }

    @Override
    @NotNull
    public CraftingRecipeRegistry<ItemStack> getRecipeRegistry() {
        return this.registry;
    }

    @Override
    @NotNull
    public String getPlatform() {
        return "Bukkit";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "4.0.0"; // Could be loaded from build info
    }

    @Override
    public void shutdown() {
        if (!this.initialized) {
            return;
        }

        this.manager.clearAllRecipes();
        this.initialized = false;
        this.plugin.getLogger().info("BukkitCraftingLib shut down");
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @NotNull
    public Plugin getPlugin() {
        return this.plugin;
    }
}
