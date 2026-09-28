package dev.piotrulla.craftinglib.recipe.book;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * How a recipe shows up in the client recipe book. Purely cosmetic: platforms without support ignore it.
 */
public final class RecipeBookSettings {

    public static final String NO_GROUP = "";
    public static final RecipeBookSettings DEFAULT = new RecipeBookSettings(true, NO_GROUP, null);

    private final boolean discoverable;
    private final String group;
    private final RecipeBookCategory category;

    /**
     * @param discoverable true when the recipe should be unlocked in the recipe book of every player
     * @param group        recipes with the same group are shown as one entry, {@link #NO_GROUP} for none
     * @param category     recipe book tab, {@code null} for the platform default
     */
    public RecipeBookSettings(boolean discoverable, @NotNull String group, @Nullable RecipeBookCategory category) {
        this.discoverable = discoverable;
        this.group = Objects.requireNonNull(group, "group");
        this.category = category;
    }

    public boolean isDiscoverable() {
        return this.discoverable;
    }

    /**
     * @return group name, {@link #NO_GROUP} when not grouped
     */
    @NotNull
    public String getGroup() {
        return this.group;
    }

    public boolean hasGroup() {
        return !this.group.isEmpty();
    }

    @NotNull
    public Optional<RecipeBookCategory> getCategory() {
        return Optional.ofNullable(this.category);
    }

    /**
     * @return true when group or category is set
     */
    public boolean hasDisplaySettings() {
        return this.hasGroup() || this.category != null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RecipeBookSettings)) {
            return false;
        }

        RecipeBookSettings that = (RecipeBookSettings) other;
        return this.discoverable == that.discoverable
                && this.group.equals(that.group)
                && this.category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.discoverable, this.group, this.category);
    }

    @Override
    public String toString() {
        return "RecipeBookSettings{discoverable=" + this.discoverable + ", group='" + this.group
                + "', category=" + this.category + '}';
    }
}
