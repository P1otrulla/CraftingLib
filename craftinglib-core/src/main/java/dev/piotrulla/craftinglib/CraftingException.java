package dev.piotrulla.craftinglib;

/**
 * Thrown when a recipe cannot be registered, unregistered or (de)serialized.
 */
public class CraftingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CraftingException(String message) {
        super(message);
    }

    public CraftingException(String message, Throwable cause) {
        super(message, cause);
    }
}
