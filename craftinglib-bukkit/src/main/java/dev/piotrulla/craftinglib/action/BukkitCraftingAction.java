package dev.piotrulla.craftinglib.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

/**
 * Base class for Bukkit crafting events.
 */
public abstract class BukkitCraftingAction implements CraftingAction {

    protected final Player player;
    protected final Instant timestamp;

    protected BukkitCraftingAction(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null");
        this.timestamp = Instant.now();
    }

    @NotNull
    public Player player() {
        return this.player;
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }
}