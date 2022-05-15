package net.osnixer.craftinglib;

import org.bukkit.inventory.ItemStack;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftingManager {

    private final Map<String, Crafting> craftings = new ConcurrentHashMap<>();

    public void createCrafting(String craftingName, Crafting crafting){
        if (this.craftings.containsKey(craftingName)){
            throw new CraftingException("Crafting with this name exists!");
        }
        this.craftings.put(craftingName, crafting);
    }

    public void removeCrafting(String craftingName){
        if (!this.craftings.containsKey(craftingName)){
            throw new CraftingException("Crafting with this name not exists");
        }
        Crafting crafting = this.craftings.get(craftingName);

        this.craftings.remove(craftingName);
        CraftingUtils.removeRecipe(crafting.getResult());
    }

    public Option<Crafting> findCrafting(ItemStack itemStack){
        return PandaStream.of(this.craftings.values())
                .filter(craft -> craft.getResult().isSimilar(itemStack))
                .head();
    }

    public Option<Crafting> findCrafting(String craftingName){
        if (this.craftings.get(craftingName) == null){
            return Option.none();
        }
        return Option.of(this.craftings.get(craftingName));
    }

    public Map<String, Crafting> getCraftings() {
        return Collections.unmodifiableMap(this.craftings);
    }
}
