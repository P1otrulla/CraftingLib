package dev.piotrulla.craftinglib;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CraftingManager {

    private final Map<String, Crafting> craftings = new HashMap<>();

    private final CraftingRegistry craftingRegistry;

    public CraftingManager(CraftingRegistry craftingRegistry) {
        this.craftingRegistry = craftingRegistry;
    }

    public void createCrafting(String craftingName, Crafting crafting) {
        if (this.craftings.containsKey(craftingName)){
            throw new CraftingException("Crafting with name "+ craftingName +" exists!");
        }

        this.craftings.put(craftingName, crafting);

        this.craftingRegistry.addCrafting(crafting);
    }

    public void removeCrafting(String craftingName) {
        if (!this.craftings.containsKey(craftingName)) {
            throw new CraftingException("Crafting with name " + craftingName + " not exists");
        }

        Crafting crafting = this.craftings.get(craftingName);

        this.craftings.remove(craftingName);

        this.craftingRegistry.removeCrafting(crafting);
    }

    public Optional<Crafting> findCrafting(ItemStack itemStack) {
        return this.craftings
                .values()
                .stream()
                .filter(crafting -> crafting.result().isSimilar(itemStack))
                .findFirst();
    }

    public Optional<Crafting> findCrafting(String craftingName) {
        if (this.craftings.get(craftingName) == null){
            return Optional.empty();
        }

        return Optional.of(this.craftings.get(craftingName));
    }

    public Collection<Crafting> craftings() {
        return Collections.unmodifiableCollection(this.craftings.values());
    }
}
