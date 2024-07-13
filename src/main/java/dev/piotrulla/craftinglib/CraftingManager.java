package dev.piotrulla.craftinglib;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public void createCrafting(String craftingName, Crafting crafting) {
        if (!crafting.name().equals(craftingName)) {
            throw new CraftingException("Crafting name must be the same as crafting name in crafting object");
        }

        this.createCrafting(crafting);
    }

    public void createCrafting(Crafting crafting) {
        String craftingName = crafting.name();

        if (this.craftingsById.containsKey(craftingName)) {
            throw new CraftingException("Crafting with name "+ craftingName +" exists!");
        }

        this.craftingsById.put(craftingName, crafting);
        this.craftingsByResult.put(crafting.result(), crafting);

        this.craftingRegistry.addCrafting(crafting);
    }

    public void removeCrafting(String craftingName) {
        if (!this.craftingsById.containsKey(craftingName)) {
            throw new CraftingException("Crafting with name " + craftingName + " not exists");
        }

        Crafting crafting = this.craftingsById.get(craftingName);

        this.craftingsById.remove(craftingName);
        this.craftingsByResult.remove(crafting.result());

        this.craftingRegistry.removeCrafting(crafting);
    }

    public Crafting findCrafting(ItemStack itemStack) {
        if (!this.craftingsByResult.containsKey(itemStack)) {
            throw new CraftingException("Crafting with result " + itemStack + " not exists");
        }

        return this.craftingsByResult.get(itemStack);
    }

    public Crafting findCrafting(String craftingName) {
        if (!this.craftingsById.containsKey(craftingName)) {
            throw new CraftingException("Crafting with name " + craftingName + " not exists");
        }

        return this.craftingsById.get(craftingName);
    }

    public Collection<Crafting> craftings() {
        return Collections.unmodifiableCollection(this.craftingsById.values());
    }
}
