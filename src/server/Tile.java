package server;

import java.awt.Point;

import common.Constants;

public class Tile extends Point {
    
    private final boolean isCollidable;
    private final boolean isSpawnPoint;

    Tile(int id, int row, int column) {
        super(column, row);
        isCollidable = Constants.COLLIDABLE_IDS.contains(id);
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
