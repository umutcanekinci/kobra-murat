package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import common.Object;
import common.Position;
import common.Constants;
public class Tile extends Object{

    private final boolean isCollidable;
    private final boolean isSpawnPoint;

    Tile(int id, int row, int column, BufferedImage image) {
        super(new Position(column, row));
        super.setImage(image);
        isSpawnPoint = id == Constants.SPAWN_TILE;
        isCollidable = Constants.COLLIDABLE_IDS.contains(id);
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

    @Override
    public void drawCollider(Graphics2D renderer) {
        if(renderer == null)
            throw new IllegalArgumentException("Graphics2D cannot be null");

        if(!isCollidable)
            return;

        super.drawCollider(renderer, Color.RED);
    }

}
