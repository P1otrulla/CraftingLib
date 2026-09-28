package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.bukkit.book.RecipeBook;
import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Everything a registrar needs from the platform, passed as one immutable object.
 */
public final class RegistrarContext {

    private final Plugin plugin;
    private final VersionAdapter versionAdapter;
    private final RecipeBook recipeBook;

    public RegistrarContext(@NotNull Plugin plugin, @NotNull VersionAdapter versionAdapter, @NotNull RecipeBook recipeBook) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.versionAdapter = Objects.requireNonNull(versionAdapter, "versionAdapter");
        this.recipeBook = Objects.requireNonNull(recipeBook, "recipeBook");
    }

    @NotNull
    public Plugin getPlugin() {
        return this.plugin;
    }

    @NotNull
    public Server getServer() {
        return this.plugin.getServer();
    }

    @NotNull
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    @NotNull
    public VersionAdapter getVersionAdapter() {
        return this.versionAdapter;
    }

    @NotNull
    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }
}
