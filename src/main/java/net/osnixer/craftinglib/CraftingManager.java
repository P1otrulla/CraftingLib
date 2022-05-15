package net.osnixer.craftinglib;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CraftingManager {

    private final Map<String, Crafting> craftings = new ConcurrentHashMap<>();

    public void createCrafting(String craftingName, Crafting crafting) {
        if (this.craftings.containsKey(craftingName)){
            throw new CraftingException("Crafting with this name exists!");
        }

        this.craftings.put(craftingName, crafting);
    }

    public void removeCrafting(String craftingName) {
        if (!this.craftings.containsKey(craftingName)) {
            throw new CraftingException("Crafting with this name not exists");
        }

        Crafting crafting = this.craftings.get(craftingName);

        this.craftings.remove(craftingName);
        CraftingUtils.removeRecipe(crafting.getResult());
    }

    public Optional<Crafting> findCrafting(ItemStack itemStack) {
        return this.craftings
                .values()
                .stream()
                .filter(crafting -> crafting.getResult().isSimilar(itemStack))
                .findFirst();
    }

    public Optional<Crafting> findCrafting(String craftingName) {
        if (this.craftings.get(craftingName) == null){
            return Optional.empty();
        }

        return Optional.of(this.craftings.get(craftingName));
    }

    public Map<String, Crafting> getCraftings() {
        return Collections.unmodifiableMap(this.craftings);
    }
}
