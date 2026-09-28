package dev.piotrulla.craftinglib.bukkit.item;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.item.MatchMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

/**
 * Maps {@link MatchMode} to {@link RecipeChoice}. Minecraft 1.14+ only - never load it on older servers.
 */
public final class RecipeChoices {

    private RecipeChoices() {
    }

    @NotNull
    public static RecipeChoice of(@NotNull ItemStack item, @NotNull MatchMode matchMode) {
        switch (matchMode) {
            case EXACT:
                return new RecipeChoice.ExactChoice(item);
            case TYPE:
                return new RecipeChoice.MaterialChoice(item.getType());
            case PERSISTENT_DATA:
                throw new CraftingException("Match mode " + matchMode + " cannot be enforced by native server recipes"
                        + " (smelting, stonecutting, smithing), use " + MatchMode.EXACT + " or " + MatchMode.TYPE);
            default:
                throw new IllegalArgumentException("Unsupported match mode: " + matchMode);
        }
    }
}