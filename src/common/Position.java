package common;

import java.awt.Point;

public class Position extends Point {

    /*
     * This class presents a position as row and column.
    */
    private boolean isScreenPosition = false;

    public Position() {
        super(0, 0);
    }

    public Position(int column, int row) {
        super(column, row);
        isScreenPosition = false;
    }

    public Position(Point point) {
        this(point.x, point.y);
        isScreenPosition = false;
    }

    public Position setScreenPosition(int x, int y) {
        this.x = x;
        this.y = y;
        isScreenPosition = true;
        return this;
    }

    public Position getScreenPosition() {
        if (isScreenPosition)
            return this;

        return new Position(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
