package common;

import java.awt.Point;

public class Position extends Point {

    /*
     * This class presents a position as row and column.
    */

    public Position() {
        super();
    }

    public Position(int column, int row) {
        super(column, row);
    }

    public Position(Position position) {
        super(position);
    }

    public Position(Point point) {
        super(point);
    }

    public Position getScreenPosition() {
        return new Position(x * common.Constants.TILE_SIZE, y * common.Constants.TILE_SIZE);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
