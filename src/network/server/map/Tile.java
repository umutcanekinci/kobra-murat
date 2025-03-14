package network.server.map;

import java.awt.*;
import java.util.ArrayList;


public class Tile extends Point {
    
    private final boolean isCollidable;
    private static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(1);
    }};

    Tile(int id, int row, int column) {
        super(column, row);
        isCollidable = COLLIDABLE_IDS.contains(id);
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public boolean doesCollide(Point point) {
        return isCollidable && equals(point);
    }
}
