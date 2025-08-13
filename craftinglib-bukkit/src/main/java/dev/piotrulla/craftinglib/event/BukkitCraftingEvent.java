package dev.piotrulla.craftinglib.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

/**
 * Base class for Bukkit crafting events.
 */
public abstract class BukkitCraftingEvent implements CraftingEvent {

    protected final Player player;
    protected final Instant timestamp;

    protected BukkitCraftingEvent(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null");
        this.timestamp = Instant.now();
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }
}