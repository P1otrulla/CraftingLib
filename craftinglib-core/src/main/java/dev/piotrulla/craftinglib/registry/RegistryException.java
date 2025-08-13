package dev.piotrulla.craftinglib.registry;

import dev.piotrulla.craftinglib.exception.CraftingException;

public class RegistryException extends CraftingException {

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
