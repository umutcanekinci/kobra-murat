package common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

public class Constants {

    /* 
     * There will be all the constants that will be used in different classes.
     * Class specified constants will be in the class itself, but the general ones will be here.
     */

    // Window Titles
    public static final String TITLE = "Kobra Murat";
    public static final String EDITOR_TITLE = "Kobra Murat Level Editor";
    public static final String SERVER_TITLE = "Kobra Murat Server";

    public static final Color BACKGROUND_COLOR = Color.GRAY;

    public static final int PORT = 7777;

    // FPS
    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    // UI Layout
    public static final Dimension GRID_SIZE = new Dimension(20, 10);
    public static final int PLAYER_BOARD_WIDTH = 200;
    public static final Dimension DEFAULT_SIZE = new Dimension(1920, 1080);
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static final double SCALEW = (float) SCREEN_SIZE.width / DEFAULT_SIZE.width;
    public static final double SCALEH = (float) SCREEN_SIZE.height / DEFAULT_SIZE.height;
    
    // Player
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    public static final int DEFAULT_LENGTH = 12;
    public static final Position HIDDEN_PART_POSITION = new Position(-1, -1);

    // Tilemap
    public static final int TILE_SIZE = 64;
    public static final int SPAWN_TILE_ID = -2;    
    public static final ArrayList<Integer> COLLIDABLE_TILE_IDS = new ArrayList<>() {{
        add(1);
    }};
}
