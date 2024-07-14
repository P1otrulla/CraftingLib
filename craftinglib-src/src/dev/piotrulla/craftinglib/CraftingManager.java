package dev.piotrulla.craftinglib;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class CraftingManager {

    private final Map<String, Crafting> craftingsById = new HashMap<>();
    private final Map<ItemStack, Crafting> craftingsByResult = new HashMap<>();

    private final CraftingRegistry craftingRegistry;

    public CraftingManager(CraftingRegistry craftingRegistry) {
        this.craftingRegistry = craftingRegistry;
    }

    /**
     * @deprecated Use {@link #createCrafting(Crafting)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "3.2.0")
    public void createCrafting(String craftingName, Crafting crafting) {
        if (!crafting.name().equals(craftingName)) {
            throw new CraftingException("Crafting name must be the same as crafting name in crafting object");
        }

        this.createCrafting(crafting);
    }

    public void createCrafting(Crafting crafting) {
        String craftingName = crafting.name();

        if (this.craftingsById.containsKey(craftingName)) {
            this.removeCrafting(craftingName);
        }

        this.craftingsById.put(craftingName, crafting);
        this.craftingsByResult.put(crafting.result(), crafting);

        this.craftingRegistry.addCrafting(crafting);
    }

    @Nullable
    public Crafting removeCrafting(String craftingName) {
        Crafting crafting = this.craftingsById.get(craftingName);

        if (crafting == null) {
            return null;
        }

        this.craftingsById.remove(craftingName);
        this.craftingsByResult.remove(crafting.result());

        this.craftingRegistry.removeCrafting(crafting);
        return crafting;
    }

    @Nullable
    public Crafting findCrafting(ItemStack itemStack) {
        return this.craftingsByResult.get(itemStack);
    }

    @Nullable
    public Crafting findCrafting(String craftingName) {
        return this.craftingsById.get(craftingName);
    }

    @Contract(pure = true)
    public boolean hasCrafting(String craftingName) {
        return this.craftingsById.containsKey(craftingName);
    }

    @Contract(pure = true)
    public boolean hasCrafting(ItemStack itemStack) {
        return this.craftingsByResult.containsKey(itemStack);
    }

    @NotNull
    @UnmodifiableView
    public Collection<Crafting> craftings() {
        return Collections.unmodifiableCollection(this.craftingsById.values());
    }

}
