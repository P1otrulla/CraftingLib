package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Class representing crafting
 */
public class Crafting {

    private final ItemStack[] ingredients;
    private final ItemStack result;
    private final String name;

    private Crafting(ItemStack[] ingredients, ItemStack result, String name) {
        this.ingredients = ingredients;
        this.result = result;
        this.name = name;
    }

    /**
     * @return Array of items in crafting table
     */
    public ItemStack[] ingredients() {
        return this.ingredients;
    }

    /**
     * @return Result of crafting
     */
    public ItemStack result() {
        return this.result;
    }

    /**
     * @return Name of crafting
     */
    public String name() {
        return this.name;
    }

    /**
     * @return Builder of crafting
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return True, if crafting is custom, means that item has custom name, lore or amount is greater than 1
     */
    public boolean isCustom() {
        for (ItemStack itemStack : this.ingredients) {
            if (itemStack != null && itemStack.hasItemMeta()) {
                return true;
            }

            if (itemStack.getAmount() > 1) {
                return true;
            }
        }

        return false;
    }

    public static class Builder {

        private final ItemStack[] itemArray = new ItemStack[9];
        private ItemStack result;
        private String name;

        /**
         * @param index Index of item in crafting table
         * @param itemStack Item to set
         * @return Builder of crafting
         */
        public Builder withItem(int index, ItemStack itemStack) {
            this.itemArray[index - 1] = itemStack;

            return this;
        }

        /**
         * @param index Index of material in crafting table
         * @param material Material of item to set
         * @return Builder of crafting
         */
        public Builder withMaterial(int index, Material material) {
            this.itemArray[index - 1] = new ItemStack(material);

            return this;
        }

        /**
         * @param index Index of material in crafting table
         * @param material Material of item to set
         * @param amount Amount of item to set
         * @return Builder of crafting
         */
        public Builder withMaterial(int index, Material material, int amount) {
            this.itemArray[index - 1] = new ItemStack(material, amount);

            return this;
        }

        /**
         * @param itemStacks array itemstack of crafting
         * @return Builder of crafting
         */
        public Builder withItems(ItemStack[] itemStacks) {
            for (int i = 0; i <= 8; i++) {
                this.itemArray[i] = itemStacks[i];
            }

            return this;
        }

        /**
         * @param itemStacks list itemstack of crafting
         * @return Builder of crafting
         */
        public Builder withItems(List<ItemStack> itemStacks) {
            for (int i = 0; i <= 8; i++) {
                this.itemArray[i] = itemStacks.get(i);
            }

            return this;
        }

        /**
         * @param materials array material of crafting
         * @return Builder of crafting
         */
        public Builder withMaterials(Material[] materials) {
            for (int i = 0; i <= 8; i++) {
                this.itemArray[i] = new ItemStack(materials[i]);
            }

            return this;
        }

        /**
         * @param materials list material of crafting
         * @return Builder of crafting
         */
        public Builder withMaterials(List<Material> materials) {
            for (int i = 0; i <= 8; i++) {
                this.itemArray[i] = new ItemStack(materials.get(i));
            }

            return this;
        }

        /**
         * @param itemStack Result of crafting
         * @return Builder of crafting
         */
        public Builder withResultItem(ItemStack itemStack) {
            this.result = itemStack;

            return this;
        }

        /**
         * @param material Result of crafting
         * @return Builder of crafting
         */
        public Builder withResultMaterial(Material material) {
            this.result = new ItemStack(material);

            return this;
        }

        /**
         * @param material Result of crafting
         * @param amount Amount of result
         * @return Builder of crafting
         */
        public Builder withResultMaterial(Material material, int amount) {
            this.result = new ItemStack(material, amount);

            return this;
        }

        /**
         * @param name Name of crafting
         * @return Builder of crafting
         */
        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Crafting build() {
            if (this.name == null) {
                throw new CraftingException("Name cannot be null");
            }
            if (this.result == null) {
                throw new CraftingException("Result item cannot be null");
            }

            return new Crafting(this.itemArray, this.result, this.name);
        }

    }

}