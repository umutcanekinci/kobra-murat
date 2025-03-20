package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import common.Object;
import common.Position;

public class Tile extends Object{

    public final boolean isCollidable;
    public final boolean isSpawnPoint;
    private static final int SPAWN_POINT = -2;

    private static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(0);
    }};

    Tile(int id, int row, int column, BufferedImage image) {
        super(new Position(row, column));
        isSpawnPoint = id == SPAWN_POINT;
        super.setImage(image);
        isCollidable = COLLIDABLE_IDS.contains(id);
    }

    @Override
    public boolean doesCollide(Position point) {
        return isCollidable && super.doesCollide(point);
    }

    @Override
    public void draw(Graphics2D renderer, ImageObserver observer) {
        if(!isCollidable)
            return;

        super.draw(renderer, observer);
    }   

    @Override
    public void drawCollider(Graphics2D renderer) {
        if(!isCollidable)
            return;

        super.drawCollider(renderer, Color.RED);
    }

}
