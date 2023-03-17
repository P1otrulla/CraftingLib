package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Class representing crafting
 */
public class Crafting {

    private final ItemStack[] itemArray;
    private final ItemStack result;
    private final String group;

    private Crafting(ItemStack[] itemArray, ItemStack result, String group) {
        this.itemArray = itemArray;
        this.result = result;
        this.group = group;
    }

    /**
     * @return Array of items in crafting table
     */
    public ItemStack[] items() {
        return this.itemArray;
    }

    /**
     * @return Result of crafting
     */
    public ItemStack result() {
        return this.result;
    }

    /**
     * @return Group of crafting
     */
    public String group() {
        return this.group;
    }

    /**
     * @return Builder of crafting
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return True if crafting is custom
     */
    public boolean isCustom() {
        for (ItemStack itemStack : this.itemArray) {
            if (itemStack != null && itemStack.hasItemMeta()){
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
        private String group;

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
         * @param group Group of crafting
         *
         * @return Builder of crafting
         */
        public Builder withGroup(String group) {
            this.group = group;

            return this;
        }

        public Crafting build() {
            if (this.result == null) {
                throw new CraftingException("Result item cannot be null");
            }

            if (this.group == null) {
                this.group = "";
            }

            return new Crafting(this.itemArray, this.result, this.group);
        }

    }

}