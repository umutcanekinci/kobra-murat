package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import common.Object;
import common.Position;
import common.Constants;
public class Tile extends Object{

    public final boolean isCollidable;
    public final boolean isSpawnPoint;

    private static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(0);
    }};

    Tile(int id, int row, int column, BufferedImage image) {
        super(new Position(column, row));
        super.setImage(image);
        isSpawnPoint = id == Constants.SPAWN_TILE;
        isCollidable = COLLIDABLE_IDS.contains(id);
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public boolean isSpawnPoint() {
        return isSpawnPoint;
    }

    @Override
    public boolean doesCollide(Position point) {
        return isCollidable && super.doesCollide(point);
    }

    @Override
    public void drawCollider(Graphics2D renderer) {
        if(!isCollidable)
            return;

        super.drawCollider(renderer, Color.RED);
    }

}
