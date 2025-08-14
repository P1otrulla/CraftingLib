package dev.piotrulla.craftinglib.action;

import dev.piotrulla.craftinglib.BukkitRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitRecipeTypedAction extends BukkitRecipeAction {

    private final BukkitRecipe recipe;
    private final RecipeAction.Type type;
    private final ItemStack result;
    private final int amount;

    public BukkitRecipeTypedAction(
            @NotNull Player player,
            @NotNull BukkitRecipe recipe,
            @NotNull RecipeAction.Type type,
            @NotNull ItemStack result,
            int amount
    ) {
        super(player);
        this.recipe = Objects.requireNonNull(recipe, "Recipe cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.amount = amount;
    }

    public @NotNull BukkitRecipe recipe() {
        return this.recipe;
    }

    @Override
    public @NotNull RecipeAction.Type type() {
        return this.type;
    }

    public @NotNull ItemStack result() {
        return this.result;
    }

    public int amount() {
        return this.amount;
    }
}
