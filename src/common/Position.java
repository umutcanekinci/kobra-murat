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
        super(row, column);
    }

    public Position(Position position) {
        super(position);
    }

    public Position(Point point) {
        super(point);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
