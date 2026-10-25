package dev.piotrulla.craftingshowcase.gui;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import dev.piotrulla.craftingshowcase.item.ItemBuilder;
import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Shows one recipe laid out like its station: the crafting grid, the furnace input and fuel, the anvil slots...
 * Empty slots of the layout stay empty, everything else is background, so the station shape stands out.
 */
public final class RecipePreviewMenu extends Menu {

    private static final int ROWS = 5;
    private static final int STATION_SLOT = 4;
    private static final int GRID_FIRST_ROW = 1;
    private static final int GRID_FIRST_COLUMN = 1;
    private static final int TOP_LEFT_SLOT = 10;
    private static final int TOP_SLOT = 11;
    private static final int MIDDLE_LEFT_SLOT = 19;
    private static final int MIDDLE_SLOT = 20;
    private static final int MIDDLE_RIGHT_SLOT = 21;
    private static final int BOTTOM_SLOT = 29;
    private static final int[] ARROW_SLOTS = {23, 24};
    private static final int RESULT_SLOT = 25;
    private static final int INFO_SLOT = 32;
    private static final int BACK_SLOT = 36;
    private static final int CLOSE_SLOT = 44;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final String ARROW_NAME = "→";
    private static final String TITLE_SEPARATOR = " » ";

    private final ShowcaseMenus menus;
    private final RecipeShowcase showcase;
    private final CustomRecipe<ItemStack> recipe;
    private final RecipeDescriber describer;

    RecipePreviewMenu(
            @NotNull ShowcaseMenus menus,
            @NotNull RecipeShowcase showcase,
            @NotNull CustomRecipe<ItemStack> recipe,
            @NotNull RecipeDescriber describer
    ) {
        super(ROWS, Component.text(Objects.requireNonNull(showcase, "showcase").getTitle() + TITLE_SEPARATOR
                + ItemNames.name(Objects.requireNonNull(recipe, "recipe").getResult()), NamedTextColor.DARK_GRAY));
        this.menus = Objects.requireNonNull(menus, "menus");
        this.showcase = showcase;
        this.recipe = recipe;
        this.describer = Objects.requireNonNull(describer, "describer");
    }

    @Override
    protected void render(@NotNull Inventory inventory) {
        if (this.recipe instanceof CraftingRecipe) {
            this.renderCrafting(inventory, (CraftingRecipe<ItemStack>) this.recipe);
        }
        else if (this.recipe instanceof SmeltingRecipe) {
            this.renderSmelting(inventory, (SmeltingRecipe<ItemStack>) this.recipe);
        }
        else if (this.recipe instanceof BrewingRecipe) {
            this.renderBrewing(inventory, (BrewingRecipe<ItemStack>) this.recipe);
        }
        else if (this.recipe instanceof StonecuttingRecipe) {
            this.renderStonecutting(inventory, (StonecuttingRecipe<ItemStack>) this.recipe);
        }
        else if (this.recipe instanceof SmithingRecipe) {
            this.renderSmithing(inventory, (SmithingRecipe<ItemStack>) this.recipe);
        }
        else if (this.recipe instanceof AnvilRecipe) {
            this.renderAnvil(inventory, (AnvilRecipe<ItemStack>) this.recipe);
        }
        else if (this.recipe instanceof GrindstoneRecipe) {
            this.renderGrindstone(inventory, (GrindstoneRecipe<ItemStack>) this.recipe);
        }

        inventory.setItem(RESULT_SLOT, this.resultIcon());
        inventory.setItem(BACK_SLOT, this.button(Material.ARROW, "Back", "Recipes of " + this.showcase.getTitle()));
        inventory.setItem(CLOSE_SLOT, this.button(Material.BARRIER, "Close"));
    }

    @Override
    public void handleClick(@NotNull Player player, int slot, @NotNull ClickType click) {
        if (slot == BACK_SLOT) {
            this.menus.openStation(player, this.showcase);
        }
        else if (slot == CLOSE_SLOT) {
            this.menus.close(player);
        }
    }

    private void renderCrafting(Inventory inventory, CraftingRecipe<ItemStack> craftingRecipe) {
        int size = CraftingGrid.WORKBENCH_SIZE;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                inventory.setItem(this.gridSlot(column, row), null);
            }
        }

        Map<Integer, ItemStack> layout = craftingRecipe.getGridLayout(size);
        layout.forEach((index, ingredient) -> inventory.setItem(this.gridSlot(index % size, index / size), ingredient));

        String station = craftingRecipe.fitsGrid(CraftingGrid.INVENTORY_SIZE)
                ? "Crafting table or player inventory"
                : "Crafting table";
        this.station(inventory, Material.CRAFTING_TABLE, station);
        this.arrow(inventory, ItemNames.readable(craftingRecipe.getType().name()));
    }

    private void renderSmelting(Inventory inventory, SmeltingRecipe<ItemStack> smeltingRecipe) {
        inventory.setItem(TOP_SLOT, smeltingRecipe.getInput());
        inventory.setItem(MIDDLE_SLOT, this.label(Material.BLAZE_POWDER, "Burning"));
        inventory.setItem(BOTTOM_SLOT, this.label(Material.COAL, "Any fuel"));

        this.station(inventory, this.stationOf(smeltingRecipe), ItemNames.readable(smeltingRecipe.getCookingType().name()));
        this.arrow(inventory, String.format(Locale.ROOT, "%.1fs, %.2f xp",
                smeltingRecipe.getCookingTime() / TICKS_PER_SECOND, smeltingRecipe.getExperience()));
    }

    private void renderBrewing(Inventory inventory, BrewingRecipe<ItemStack> brewingRecipe) {
        inventory.setItem(TOP_SLOT, brewingRecipe.getIngredient());
        inventory.setItem(TOP_LEFT_SLOT, this.label(Material.BLAZE_POWDER, "Fuel"));
        inventory.setItem(BOTTOM_SLOT, brewingRecipe.getInput());

        this.station(inventory, Material.BREWING_STAND, "Brewing stand");
        this.arrow(inventory, "Brewing");
    }

    private void renderStonecutting(Inventory inventory, StonecuttingRecipe<ItemStack> stonecuttingRecipe) {
        inventory.setItem(MIDDLE_SLOT, stonecuttingRecipe.getInput());

        this.station(inventory, Material.STONECUTTER, "Stonecutter");
        this.arrow(inventory, "Cut");
    }

    private void renderSmithing(Inventory inventory, SmithingRecipe<ItemStack> smithingRecipe) {
        inventory.setItem(MIDDLE_LEFT_SLOT, smithingRecipe.getTemplate().orElse(null));
        inventory.setItem(MIDDLE_SLOT, smithingRecipe.getBase());
        inventory.setItem(MIDDLE_RIGHT_SLOT, smithingRecipe.getAddition());

        this.station(inventory, Material.SMITHING_TABLE, "Smithing table");
        this.arrow(inventory, "Upgrade");
    }

    private void renderAnvil(Inventory inventory, AnvilRecipe<ItemStack> anvilRecipe) {
        inventory.setItem(MIDDLE_LEFT_SLOT, anvilRecipe.getLeft());
        inventory.setItem(MIDDLE_SLOT, this.label(Material.GRAY_STAINED_GLASS_PANE, "+"));
        inventory.setItem(MIDDLE_RIGHT_SLOT, anvilRecipe.getRight());
        inventory.setItem(INFO_SLOT, this.label(Material.EXPERIENCE_BOTTLE, anvilRecipe.getLevelCost() + " levels"));

        this.station(inventory, Material.ANVIL, "Anvil");
        this.arrow(inventory, "Costs " + anvilRecipe.getLevelCost() + " levels");
    }

    private void renderGrindstone(Inventory inventory, GrindstoneRecipe<ItemStack> grindstoneRecipe) {
        inventory.setItem(TOP_SLOT, grindstoneRecipe.getInput());
        inventory.setItem(BOTTOM_SLOT, grindstoneRecipe.getSecondInput().orElse(null));
        inventory.setItem(INFO_SLOT, this.label(Material.EXPERIENCE_BOTTLE, grindstoneRecipe.getExperience() + " experience"));

        this.station(inventory, Material.GRINDSTONE, "Grindstone (inputs in either slot)");
        this.arrow(inventory, "Grind");
    }

    private Material stationOf(SmeltingRecipe<ItemStack> smeltingRecipe) {
        switch (smeltingRecipe.getCookingType()) {
            case BLASTING:
                return Material.BLAST_FURNACE;
            case SMOKING:
                return Material.SMOKER;
            case CAMPFIRE:
                return Material.CAMPFIRE;
            case FURNACE:
            default:
                return Material.FURNACE;
        }
    }

    /**
     * The result with the full recipe description below its own lore.
     */
    private ItemStack resultIcon() {
        ItemStack icon = this.recipe.getResult();
        ItemMeta meta = icon.getItemMeta();

        List<Component> lore = new ArrayList<>();
        if (meta.lore() != null) {
            lore.addAll(meta.lore());
            lore.add(Component.empty());
        }
        lore.addAll(this.describer.describe(this.recipe));

        meta.lore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private void station(Inventory inventory, Material material, String name) {
        inventory.setItem(STATION_SLOT, ItemBuilder.of(material)
                .name(name, NamedTextColor.GOLD)
                .lore("Recipe key: " + this.recipe.getKey(), NamedTextColor.GRAY)
                .build());
    }

    private void arrow(Inventory inventory, String info) {
        ItemStack arrow = ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE)
                .name(ARROW_NAME, NamedTextColor.GREEN)
                .lore(info, NamedTextColor.GRAY)
                .build();
        for (int slot : ARROW_SLOTS) {
            inventory.setItem(slot, arrow);
        }
    }

    private ItemStack label(Material material, String name) {
        return ItemBuilder.of(material).name(name, NamedTextColor.YELLOW).build();
    }

    private int gridSlot(int column, int row) {
        return (GRID_FIRST_ROW + row) * SLOTS_PER_ROW + GRID_FIRST_COLUMN + column;
    }
}
