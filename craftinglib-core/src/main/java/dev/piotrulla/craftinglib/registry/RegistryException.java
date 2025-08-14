package dev.piotrulla.craftinglib.registry;

import dev.piotrulla.craftinglib.exception.RecipeException;

public class RegistryException extends RecipeException {

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
