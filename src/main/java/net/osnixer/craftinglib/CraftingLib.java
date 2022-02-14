package net.osnixer.craftinglib;

import org.bukkit.plugin.Plugin;

public class CraftingLib {

    private final Plugin plugin;
    private final CraftingManager craftingManager;

    public CraftingLib(Plugin plugin){
        this.plugin = plugin;

        this.craftingManager = new CraftingManager();

        plugin.getServer().getPluginManager().registerEvents(new CraftingListeners(this.craftingManager), plugin);
    }

    public CraftingManager getManager() {
        return craftingManager;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
