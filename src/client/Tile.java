package client;

import java.awt.image.BufferedImage;

import common.Object;
import common.Position;
import common.Constants;
public class Tile extends Object {

    private final boolean isCollidable;
    private final boolean isSpawnPoint;

    Tile(int id, int row, int column, BufferedImage image) {
        super(new Position(column, row));
        isSpawnPoint = id == Constants.SPAWN_TILE_ID;
        isCollidable = Constants.COLLIDABLE_TILE_IDS.contains(id);
        super.setImage(image);
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public boolean isSpawnPoint() {
        return isSpawnPoint;
    }

    @Override
    public boolean doesCollide(Position point) {
        if(point == null)
            throw new IllegalArgumentException("Position cannot be null");

        return isCollidable && super.doesCollide(point);
    }

}
