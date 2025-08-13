package dev.piotrulla.craftinglib.event;

import dev.piotrulla.craftinglib.BukkitCraftingRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitCraftingTypedEvent extends BukkitCraftingEvent {

    private final BukkitCraftingRecipe recipe;
    private final CraftingEvent.Type type;
    private final ItemStack result;
    private final int amount;

    public BukkitCraftingTypedEvent(
            @NotNull Player player,
            @NotNull BukkitCraftingRecipe recipe,
            @NotNull CraftingEvent.Type type,
            @NotNull ItemStack result,
            int amount
    ) {
        super(player);
        this.recipe = Objects.requireNonNull(recipe, "Recipe cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.amount = amount;
    }

    public @NotNull BukkitCraftingRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public @NotNull CraftingEvent.Type getType() {
        return this.type;
    }

    public @NotNull ItemStack getResult() {
        return this.result;
    }

    public int getAmount() {
        return this.amount;
    }
}
