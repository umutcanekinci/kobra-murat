package common;

import java.awt.Point;

public class Position extends Point {

    /*
     * This class presents a position as row and column.
    */

    public Position() {
        super();
    }

    public Position(int row, int column) {
        super(column, row);
    }

    public Position(Position position) {
        super(position);
    }

    public Position(Point point) {
        super(point);
    }

    public Position getScreenPosition() {
        return new Position(y * common.Level.TILE_SIZE, x * common.Level.TILE_SIZE);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
