package dev.piotrulla.craftinglib.support;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.item.MatchMode;

public final class TestItemAdapter implements ItemAdapter<TestItem> {

    public static final TestItemAdapter INSTANCE = new TestItemAdapter();

    private static final String AIR = "air";

    private TestItemAdapter() {
    }

    @Override
    public boolean isEmpty(TestItem item) {
        return item == null || AIR.equals(item.getType()) || item.getAmount() < 1;
    }

    @Override
    public int getAmount(TestItem item) {
        return item.getAmount();
    }

    @Override
    public boolean isSimilar(TestItem required, TestItem actual, MatchMode matchMode) {
        switch (matchMode) {
            case EXACT:
                return required.isSimilar(actual);
            case TYPE:
                return required.getType().equals(actual.getType());
            case PERSISTENT_DATA:
                return required.hasSameData(actual);
            default:
                throw new IllegalArgumentException("Unsupported match mode: " + matchMode);
        }
    }

    @Override
    public TestItem copy(TestItem item) {
        return item.withAmount(item.getAmount());
    }
}
