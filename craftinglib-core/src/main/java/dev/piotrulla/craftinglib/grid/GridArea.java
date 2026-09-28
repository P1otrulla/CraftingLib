package dev.piotrulla.craftinglib.grid;

/**
 * Rectangle inside a crafting grid (top-left corner + size).
 */
public final class GridArea {

    private final int column;
    private final int row;
    private final int width;
    private final int height;

    public GridArea(int column, int row, int width, int height) {
        if (column < 0 || row < 0 || width < 1 || height < 1) {
            throw new IllegalArgumentException("Invalid grid area: column=" + column + ", row=" + row
                    + ", width=" + width + ", height=" + height);
        }

        this.column = column;
        this.row = row;
        this.width = width;
        this.height = height;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GridArea)) {
            return false;
        }

        GridArea area = (GridArea) other;
        return this.column == area.column && this.row == area.row
                && this.width == area.width && this.height == area.height;
    }

    @Override
    public int hashCode() {
        int result = this.column;
        result = 31 * result + this.row;
        result = 31 * result + this.width;
        return 31 * result + this.height;
    }

    @Override
    public String toString() {
        return "GridArea{column=" + this.column + ", row=" + this.row
                + ", width=" + this.width + ", height=" + this.height + '}';
    }
}
