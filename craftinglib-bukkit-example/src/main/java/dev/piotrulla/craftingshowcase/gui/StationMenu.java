package dev.piotrulla.craftingshowcase.gui;

import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Recipes of one station: the result item with a description generated from the recipe.
 * Clicking a recipe opens its {@link RecipePreviewMenu}.
 */
public final class StationMenu extends Menu {

    private static final int ROWS = 6;
    private static final int FIRST_CONTENT_ROW = 1;
    private static final int LAST_CONTENT_ROW = 4;
    private static final int FIRST_CONTENT_COLUMN = 1;
    private static final int LAST_CONTENT_COLUMN = 7;
    private static final int BACK_SLOT = 45;
    private static final int KIT_SLOT = 49;
    private static final int CLOSE_SLOT = 53;
    private static final String TITLE_PREFIX = "CraftingLib » ";

    private final ShowcaseMenus menus;
    private final RecipeShowcase showcase;
    private final RecipeDescriber describer;
    private final Map<Integer, CustomRecipe<ItemStack>> recipeBySlot = new HashMap<>();

    StationMenu(@NotNull ShowcaseMenus menus, @NotNull RecipeShowcase showcase, @NotNull RecipeDescriber describer) {
        super(ROWS, Component.text(TITLE_PREFIX + Objects.requireNonNull(showcase, "showcase").getTitle(),
                NamedTextColor.DARK_GRAY));
        this.menus = Objects.requireNonNull(menus, "menus");
        this.showcase = showcase;
        this.describer = Objects.requireNonNull(describer, "describer");
    }

    @Override
    protected void render(@NotNull Inventory inventory) {
        List<Integer> contentSlots = this.contentSlots();
        List<CustomRecipe<ItemStack>> recipes = this.showcase.getRecipes();
        this.recipeBySlot.clear();

        for (int index = 0; index < recipes.size() && index < contentSlots.size(); index++) {
            int slot = contentSlots.get(index);
            inventory.setItem(slot, this.recipeIcon(recipes.get(index)));
            this.recipeBySlot.put(slot, recipes.get(index));
        }

        inventory.setItem(BACK_SLOT, this.button(Material.ARROW, "Back", "All stations"));
        inventory.setItem(KIT_SLOT, this.button(Material.CHEST, "Get kit",
                "Ingredients and stations for these recipes"));
        inventory.setItem(CLOSE_SLOT, this.button(Material.BARRIER, "Close"));
    }

    @Override
    public void handleClick(@NotNull Player player, int slot, @NotNull ClickType click) {
        if (slot == BACK_SLOT) {
            this.menus.openMain(player);
        }
        else if (slot == KIT_SLOT) {
            this.menus.giveKits(player, List.of(this.showcase));
        }
        else if (slot == CLOSE_SLOT) {
            this.menus.close(player);
        }
        else if (this.recipeBySlot.containsKey(slot)) {
            this.menus.openRecipe(player, this.showcase, this.recipeBySlot.get(slot));
        }
    }

    /**
     * The result keeps its own name and lore, the recipe description goes below.
     */
    private ItemStack recipeIcon(CustomRecipe<ItemStack> recipe) {
        ItemStack icon = recipe.getResult();
        ItemMeta meta = icon.getItemMeta();

        List<Component> lore = new ArrayList<>();
        if (meta.lore() != null) {
            lore.addAll(meta.lore());
            lore.add(Component.empty());
        }
        lore.addAll(this.describer.describe(recipe));
        lore.add(Component.empty());
        lore.add(Component.text("Click to see the layout", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private List<Integer> contentSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int row = FIRST_CONTENT_ROW; row <= LAST_CONTENT_ROW; row++) {
            for (int column = FIRST_CONTENT_COLUMN; column <= LAST_CONTENT_COLUMN; column++) {
                slots.add(row * SLOTS_PER_ROW + column);
            }
        }

        return slots;
    }
}
