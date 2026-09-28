package dev.piotrulla.craftinglib.recipe;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Recipe key rules. Same character set as Minecraft namespaced keys, so a key can be used on every platform as-is.
 */
public final class RecipeKeys {

    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");
    private static final int MAX_LENGTH = 128;

    private RecipeKeys() {
    }

    @NotNull
    public static String requireValid(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Recipe key cannot be null or empty");
        }
        if (key.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Recipe key cannot be longer than " + MAX_LENGTH + " characters: " + key);
        }
        if (!VALID_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Recipe key '" + key + "' may only contain [a-z0-9/._-]");
        }

        return key;
    }
}
