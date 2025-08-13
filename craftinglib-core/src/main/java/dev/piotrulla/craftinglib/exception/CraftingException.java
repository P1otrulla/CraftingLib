package dev.piotrulla.craftinglib.exception;

public class CraftingException extends RuntimeException {

    public CraftingException(String message) {
        super(message);
    }

    public CraftingException(String message, Throwable cause) {
        super(message, cause);
    }
}
