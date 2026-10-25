package dev.piotrulla.craftingshowcase.gui;

import dev.piotrulla.craftingshowcase.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Chest menu identified by its {@link InventoryHolder}; {@link MenuListener} cancels clicks and forwards them here.
 */
public abstract class Menu implements InventoryHolder {

    protected static final int SLOTS_PER_ROW = 9;
    private static final String EMPTY_NAME = " ";

    private final int rows;
    private final Component title;
    private Inventory inventory;

    protected Menu(int rows, @NotNull Component title) {
        this.rows = rows;
        this.title = Objects.requireNonNull(title, "title");
    }

    /**
     * Created on first use, so no half-constructed menu is passed to Bukkit.
     */
    @NotNull
    @Override
    public final Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, this.rows * SLOTS_PER_ROW, this.title);
        }

        return this.inventory;
    }

    public final void open(@NotNull Player player) {
        Inventory menuInventory = this.getInventory();
        menuInventory.clear();
        this.fillBackground(menuInventory);
        this.render(menuInventory);
        player.openInventory(menuInventory);
    }

    /**
     * @param slot raw slot inside this menu
     */
    public abstract void handleClick(@NotNull Player player, int slot, @NotNull ClickType click);

    protected abstract void render(@NotNull Inventory inventory);

    @NotNull
    protected ItemStack button(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
        ItemBuilder builder = ItemBuilder.of(material).name(name, NamedTextColor.YELLOW);
        for (String line : lore) {
            builder.lore(line, NamedTextColor.GRAY);
        }

        return builder.build();
    }

    private void fillBackground(Inventory menuInventory) {
        ItemStack filler = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(EMPTY_NAME, NamedTextColor.GRAY).build();
        for (int slot = 0; slot < menuInventory.getSize(); slot++) {
            menuInventory.setItem(slot, filler);
        }
    }
}
