package dev.piotrulla.craftinglib.action;

import dev.piotrulla.craftinglib.BukkitCraftingRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitCraftingTypedAction extends BukkitCraftingAction {

    private final BukkitCraftingRecipe recipe;
    private final CraftingAction.Type type;
    private final ItemStack result;
    private final int amount;

    public BukkitCraftingTypedAction(
            @NotNull Player player,
            @NotNull BukkitCraftingRecipe recipe,
            @NotNull CraftingAction.Type type,
            @NotNull ItemStack result,
            int amount
    ) {
        super(player);
        this.recipe = Objects.requireNonNull(recipe, "Recipe cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.amount = amount;
    }

    public @NotNull BukkitCraftingRecipe recipe() {
        return this.recipe;
    }

    @Override
    public @NotNull CraftingAction.Type type() {
        return this.type;
    }

    public @NotNull ItemStack result() {
        return this.result;
    }

    public int amount() {
        return this.amount;
    }
}
