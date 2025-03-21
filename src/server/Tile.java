package server;

import java.awt.*;
import java.util.ArrayList;

import common.Constants;

public class Tile extends Point {
    
    private final boolean isCollidable;
    private final boolean isSpawnPoint;
    private static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(1);
    }};

    Tile(int id, int row, int column) {
        super(column, row);
        isCollidable = COLLIDABLE_IDS.contains(id);
        isSpawnPoint = id == Constants.SPAWN_TILE;
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public boolean isSpawnPoint() {
        return isSpawnPoint;
    }

    public boolean doesCollide(Point point) {
        return isCollidable && equals(point);
    }
}
