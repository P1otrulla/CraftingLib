package dev.piotrulla.craftingshowcase.gui;

import dev.piotrulla.craftingshowcase.item.ItemBuilder;
import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * One icon per station. Left click shows its recipes, right click gives its kit.
 */
public final class MainMenu extends Menu {

    private static final int ROWS = 4;
    private static final int[] STATION_SLOTS = {10, 11, 12, 13, 14, 15, 16, 22, 21, 23, 20, 24, 19, 25};
    private static final int ALL_KITS_SLOT = 31;
    private static final Component TITLE = Component.text("CraftingLib Showcase", NamedTextColor.DARK_GRAY);

    private final ShowcaseMenus menus;
    private final List<RecipeShowcase> showcases;
    private final Map<Integer, RecipeShowcase> showcaseBySlot = new HashMap<>();

    MainMenu(@NotNull ShowcaseMenus menus, @NotNull List<RecipeShowcase> showcases) {
        super(ROWS, TITLE);
        this.menus = Objects.requireNonNull(menus, "menus");
        this.showcases = List.copyOf(showcases);

        if (this.showcases.size() > STATION_SLOTS.length) {
            throw new IllegalArgumentException("Main menu fits " + STATION_SLOTS.length + " showcases, got: "
                    + this.showcases.size());
        }
    }

    @Override
    protected void render(@NotNull Inventory inventory) {
        this.showcaseBySlot.clear();

        for (int index = 0; index < this.showcases.size(); index++) {
            RecipeShowcase showcase = this.showcases.get(index);
            int slot = STATION_SLOTS[index];

            inventory.setItem(slot, ItemBuilder.of(showcase.getIcon())
                    .name(showcase.getTitle(), NamedTextColor.GOLD)
                    .lore(showcase.getDescription(), NamedTextColor.GRAY)
                    .lore(showcase.getRecipes().size() + " recipes", NamedTextColor.DARK_AQUA)
                    .lore("Left click: show recipes", NamedTextColor.YELLOW)
                    .lore("Right click: get the kit", NamedTextColor.YELLOW)
                    .build());
            this.showcaseBySlot.put(slot, showcase);
        }

        inventory.setItem(ALL_KITS_SLOT, this.button(Material.CHEST, "Get every kit",
                "Ingredients and stations for all recipes"));
    }

    @Override
    public void handleClick(@NotNull Player player, int slot, @NotNull ClickType click) {
        if (slot == ALL_KITS_SLOT) {
            this.menus.giveKits(player, this.showcases);
            return;
        }

        RecipeShowcase showcase = this.showcaseBySlot.get(slot);
        if (showcase == null) {
            return;
        }

        if (click.isRightClick()) {
            this.menus.giveKits(player, List.of(showcase));
            return;
        }
        this.menus.openStation(player, showcase);
    }
}
