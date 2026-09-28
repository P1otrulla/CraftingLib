package dev.piotrulla.craftinglib.bukkit.book;

import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 1.13+: discovers published recipes for online players and again on every join,
 * so recipes registered while players are online show up too.
 */
public final class PlayerRecipeBook implements RecipeBook, Listener {

    private final Plugin plugin;
    private final Set<NamespacedKey> publishedRecipes = new LinkedHashSet<>();

    public PlayerRecipeBook(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public void publish(@NotNull String key) {
        NamespacedKey recipeKey = this.createKey(key);
        if (!this.publishedRecipes.add(recipeKey)) {
            return;
        }

        for (Player player : this.server().getOnlinePlayers()) {
            player.discoverRecipe(recipeKey);
        }
    }

    @Override
    public void retract(@NotNull String key) {
        NamespacedKey recipeKey = this.createKey(key);
        if (!this.publishedRecipes.remove(recipeKey)) {
            return;
        }

        for (Player player : this.server().getOnlinePlayers()) {
            player.undiscoverRecipe(recipeKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        if (!this.publishedRecipes.isEmpty()) {
            event.getPlayer().discoverRecipes(new ArrayList<>(this.publishedRecipes));
        }
    }

    private NamespacedKey createKey(String key) {
        Objects.requireNonNull(key, "key");
        return new NamespacedKey(this.plugin, key);
    }

    private Server server() {
        return this.plugin.getServer();
    }
}
