package dev.piotrulla.craftinglib.support;

import java.util.Objects;

/**
 * Minimal platform-free item used to test the core.
 * {@code name} plays the display name, {@code tag} plays custom plugin data.
 */
public final class TestItem {

    private final String type;
    private final String name;
    private final String tag;
    private final int amount;

    private TestItem(String type, String name, String tag, int amount) {
        this.type = type;
        this.name = name;
        this.tag = tag;
        this.amount = amount;
    }

    public static TestItem of(String type) {
        return of(type, 1);
    }

    public static TestItem of(String type, int amount) {
        return new TestItem(type, null, null, amount);
    }

    public TestItem named(String name) {
        return new TestItem(this.type, name, this.tag, this.amount);
    }

    public TestItem tagged(String tag) {
        return new TestItem(this.type, this.name, tag, this.amount);
    }

    public TestItem withAmount(int amount) {
        return new TestItem(this.type, this.name, this.tag, amount);
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isSimilar(TestItem other) {
        return this.hasSameData(other) && Objects.equals(this.name, other.name);
    }

    public boolean hasSameData(TestItem other) {
        return this.type.equals(other.type) && Objects.equals(this.tag, other.tag);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TestItem)) {
            return false;
        }

        TestItem item = (TestItem) other;
        return this.amount == item.amount && this.isSimilar(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name, this.tag, this.amount);
    }

    @Override
    public String toString() {
        return this.amount + "x" + this.type
                + (this.name == null ? "" : "[" + this.name + "]")
                + (this.tag == null ? "" : "{" + this.tag + "}");
    }
}
