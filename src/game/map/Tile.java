package game.map;

import java.awt.*;
import java.util.ArrayList;

public class Tile {

    private static final int WIDTH = 64;
    private static final int HEIGHT = 64;
    private static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(1);
    }};

    private int row, column;
    private int x, y;
    private boolean isCollidable = false;

    Tile(int id, int row, int column) {
        this.row = row;
        this.column = column;
        x = column * WIDTH;
        y = row * HEIGHT;
        isCollidable = COLLIDABLE_IDS.contains(id);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.row = y / HEIGHT;
        this.column = x / WIDTH;
    }

    public boolean isCollide(Point point) {
        return isCollidable && point.x == column && point.y == row;
    }

    public void render(Graphics2D renderer) {
        if(!isCollidable)
            return;

        renderer.setColor(Color.BLACK);
        renderer.fillRect(x, y, WIDTH, HEIGHT);

        // This draws border
        //renderer.draw(new Rectangle(x, y, WIDTH, HEIGHT));
    }

}
