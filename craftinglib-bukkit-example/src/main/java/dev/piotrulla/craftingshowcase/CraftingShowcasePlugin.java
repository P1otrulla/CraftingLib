package dev.piotrulla.craftingshowcase;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftingshowcase.command.ShowcaseCommand;
import dev.piotrulla.craftingshowcase.gui.MenuListener;
import dev.piotrulla.craftingshowcase.gui.ShowcaseMenus;
import dev.piotrulla.craftingshowcase.item.ShowcaseItems;
import dev.piotrulla.craftingshowcase.kit.KitGiver;
import dev.piotrulla.craftingshowcase.showcase.AnvilShowcase;
import dev.piotrulla.craftingshowcase.showcase.BrewingShowcase;
import dev.piotrulla.craftingshowcase.showcase.ConfigShowcase;
import dev.piotrulla.craftingshowcase.showcase.CraftingShowcase;
import dev.piotrulla.craftingshowcase.showcase.GrindstoneShowcase;
import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import dev.piotrulla.craftingshowcase.showcase.SmeltingShowcase;
import dev.piotrulla.craftingshowcase.showcase.SmithingShowcase;
import dev.piotrulla.craftingshowcase.showcase.StonecuttingShowcase;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Registers a showcase of every CraftingLib recipe type. {@code /showcase} opens a menu with all of them.
 */
public final class CraftingShowcasePlugin extends JavaPlugin {

    private static final String COMMAND_NAME = "showcase";

    private BukkitCraftingLib craftingLib;

    @Override
    public void onEnable() {
        this.craftingLib = BukkitCraftingLib.create(this);

        ShowcaseItems items = new ShowcaseItems(this);
        List<RecipeShowcase> showcases = List.of(
                new CraftingShowcase(items),
                new SmeltingShowcase(items),
                new BrewingShowcase(items),
                new StonecuttingShowcase(items),
                new SmithingShowcase(items),
                new AnvilShowcase(items),
                new GrindstoneShowcase(items),
                new ConfigShowcase(this.getDataFolder())
        );

        List<RecipeShowcase> registered = new ArrayList<>();
        for (RecipeShowcase showcase : showcases) {
            if (this.registerShowcase(showcase)) {
                registered.add(showcase);
            }
        }

        KitGiver kitGiver = new KitGiver();
        ShowcaseMenus menus = new ShowcaseMenus(this, registered, kitGiver);
        ShowcaseCommand showcaseCommand = new ShowcaseCommand(registered, menus, kitGiver);

        PluginCommand command = Objects.requireNonNull(this.getCommand(COMMAND_NAME),
                "Command '" + COMMAND_NAME + "' is missing from plugin.yml");
        command.setExecutor(showcaseCommand);
        command.setTabCompleter(showcaseCommand);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
    }

    @Override
    public void onDisable() {
        if (this.craftingLib != null) {
            this.craftingLib.shutdown();
        }
    }

    /**
     * A station the server does not support is skipped, the others still register.
     *
     * @return true when the showcase registered
     */
    private boolean registerShowcase(RecipeShowcase showcase) {
        try {
            showcase.register(this.craftingLib);
            this.getLogger().info("Registered '" + showcase.getName() + "' showcase");
            return true;
        }
        catch (UnsupportedOperationException | CraftingException exception) {
            this.getLogger().warning("Skipped '" + showcase.getName() + "' showcase: " + exception.getMessage());
            return false;
        }
    }
}
