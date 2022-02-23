package net.osnixer.craftinglib;

import org.bukkit.inventory.ItemStack;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftingManager {

    private final Map<String, Crafting> craftingMap = new ConcurrentHashMap<>();

    public void createCrafting(String craftingName, Crafting crafting){
        if (this.craftingMap.containsKey(craftingName)){
            throw new CraftingException("Crafting with this name exists!");
        }
        this.craftingMap.put(craftingName, crafting);
    }

    public void removeCrafting(String craftingName){
        if (!this.craftingMap.containsKey(craftingName)){
            throw new CraftingException("Crafting with this name not exists");
        }
        Crafting crafting = this.craftingMap.get(craftingName);

        this.craftingMap.remove(craftingName);
        CraftingUtils.removeRecipe(crafting.getResult());
    }

    public Option<Crafting> findCrafting(ItemStack itemStack){
        return PandaStream.of(this.craftingMap.values())
                .filter(craft -> craft.getResult().isSimilar(itemStack))
                .head();
    }

    public Option<Crafting> findCrafting(String itemStack){
        if (this.craftingMap.get(itemStack) == null){
            return Option.none();
        }
        return Option.of(this.craftingMap.get(itemStack));
    }

    public Map<String, Crafting> get() {
        return Collections.unmodifiableMap(this.craftingMap);
    }
}
