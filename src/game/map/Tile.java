package game.map;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import game.Object;

public class Tile extends Object{


    private static final File IMAGE_FILE = new File("images/wall.png");
    
    private final boolean isCollidable;
    private static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(1);
    }};

    Tile(int id, int row, int column) {
        super(new Point(column, row), IMAGE_FILE);
        isCollidable = COLLIDABLE_IDS.contains(id);
    }

    @Override
    public boolean doesCollide(Point point) {
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
