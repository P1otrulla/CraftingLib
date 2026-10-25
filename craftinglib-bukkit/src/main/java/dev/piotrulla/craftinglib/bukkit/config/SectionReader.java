package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.CraftingException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Typed, fail-fast reads from one recipe section. Every error names the recipe key and the field.
 * <p>
 * Items are read either as a serialized {@link ItemStack} (what {@link ConfigurationSection#set} writes)
 * or as a plain material name, e.g. {@code input: DIAMOND}, which is handy in hand-written configs.
 */
public final class SectionReader {

    private static final int SYMBOL_LENGTH = 1;

    private final ConfigurationSection section;
    private final String key;

    SectionReader(@NotNull ConfigurationSection section) {
        this.section = section;
        this.key = section.getName();
    }

    /**
     * @return recipe key, which is the name of the section
     */
    @NotNull
    public String getKey() {
        return this.key;
    }

    public boolean contains(@NotNull String field) {
        return this.section.contains(field);
    }

    @NotNull
    public ItemStack requireItem(@NotNull String field) {
        return this.toItem(field, this.require(field));
    }

    @NotNull
    public Optional<ItemStack> findItem(@NotNull String field) {
        return this.find(field).map(value -> this.toItem(field, value));
    }

    @NotNull
    public Optional<Boolean> findBoolean(@NotNull String field) {
        return this.find(field).map(value -> this.cast(field, value, Boolean.class, "true or false"));
    }

    @NotNull
    public Optional<String> findString(@NotNull String field) {
        return this.find(field).map(value -> this.cast(field, value, String.class, "a text"));
    }

    @NotNull
    public Optional<Integer> findInt(@NotNull String field) {
        return this.find(field).map(value -> {
            if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
                return ((Number) value).intValue();
            }

            throw this.invalid(field, "a whole number", value);
        });
    }

    @NotNull
    public Optional<Float> findFloat(@NotNull String field) {
        return this.find(field).map(value -> this.cast(field, value, Number.class, "a number").floatValue());
    }

    @NotNull
    public <E extends Enum<E>> E requireEnum(@NotNull String field, @NotNull Class<E> type) {
        return this.toEnum(field, this.require(field), type);
    }

    @NotNull
    public <E extends Enum<E>> Optional<E> findEnum(@NotNull String field, @NotNull Class<E> type) {
        return this.find(field).map(value -> this.toEnum(field, value, type));
    }

    @NotNull
    public List<String> requireStringList(@NotNull String field) {
        List<String> strings = new ArrayList<>();
        for (Object element : this.requireList(field)) {
            strings.add(this.cast(field, element, String.class, "a list of texts"));
        }

        return Collections.unmodifiableList(strings);
    }

    @NotNull
    public List<ItemStack> requireItemList(@NotNull String field) {
        List<ItemStack> items = new ArrayList<>();
        for (Object element : this.requireList(field)) {
            items.add(this.toItem(field, element));
        }

        return Collections.unmodifiableList(items);
    }

    /**
     * Reads a section of single-character symbols mapped to items, in config order.
     */
    @NotNull
    public Map<Character, ItemStack> requireSymbolMap(@NotNull String field) {
        ConfigurationSection symbols = this.section.getConfigurationSection(field);
        if (symbols == null) {
            throw this.failure("field '" + field + "' must be a section of symbol: item entries");
        }

        Map<Character, ItemStack> items = new LinkedHashMap<>();
        for (String symbol : symbols.getKeys(false)) {
            if (symbol.length() != SYMBOL_LENGTH) {
                throw this.failure("ingredient symbol must be a single character, got: '" + symbol + "'");
            }
            items.put(symbol.charAt(0), this.toItem(field + "." + symbol, symbols.get(symbol)));
        }

        return Collections.unmodifiableMap(items);
    }

    private List<?> requireList(String field) {
        Object value = this.require(field);
        if (!(value instanceof List)) {
            throw this.invalid(field, "a list", value);
        }

        return (List<?>) value;
    }

    private Object require(String field) {
        return this.find(field).orElseThrow(() -> this.failure("missing required field '" + field + "'"));
    }

    private Optional<Object> find(String field) {
        return Optional.ofNullable(this.section.get(field));
    }

    private ItemStack toItem(String field, Object value) {
        if (value instanceof ItemStack) {
            return ((ItemStack) value).clone();
        }
        if (value instanceof String) {
            Material material = Material.matchMaterial((String) value);
            if (material == null) {
                throw this.failure("field '" + field + "': unknown material '" + value + "'");
            }

            return new ItemStack(material);
        }

        throw this.invalid(field, "an item stack or a material name", value);
    }

    private <E extends Enum<E>> E toEnum(String field, Object value, Class<E> type) {
        String name = this.cast(field, value, String.class, "one of " + Arrays.toString(type.getEnumConstants()));
        try {
            return Enum.valueOf(type, name.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException exception) {
            throw this.failure("field '" + field + "' must be one of " + Arrays.toString(type.getEnumConstants()) + ", got: '" + name + "'");
        }
    }

    private <V> V cast(String field, Object value, Class<V> type, String expected) {
        if (!type.isInstance(value)) {
            throw this.invalid(field, expected, value);
        }

        return type.cast(value);
    }

    private CraftingException invalid(String field, String expected, Object value) {
        return this.failure("field '" + field + "' must be " + expected + ", got: " + value.getClass().getSimpleName() + " '" + value + "'");
    }

    private CraftingException failure(String message) {
        return new CraftingException("Recipe '" + this.key + "': " + message);
    }
}
