package common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import common.graphics.Image;

public class Constants {

    /* 
     * There will be all the constants that will be used in different classes.
     * Class specified constants will be in the class itself, but the general ones will be here.
     */

    public static final String TITLE = "Kobra Murat";
    public static final String EDITOR_TITLE = "Kobra Murat Level Editor";
    public static final String SERVER_TITLE = "Kobra Murat Server";

    public static final Color BACKGROUND_COLOR = Color.GRAY;

    public static final int PORT = 7777;

    public static final int SPAWN_TILE = -2;    
    public static final Position HIDDEN_POSITION = new Position(-1, -1);

    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    public static final int TILE_SIZE = 64;
    public static final int PLAYER_BOARD_WIDTH = 200;

    public static final Dimension MAX_SIZE = new Dimension(1920, 1080);
    public static final Dimension SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static final double SCALEW = (float) SIZE.width / MAX_SIZE.width;
    public static final double SCALEH = (float) SIZE.height / MAX_SIZE.height;
    
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    public static final int DEFAULT_LENGTH = 12;

    public static final Spritesheet TILESHEET   = new SpritesheetBuilder().withColumns(1).withRows(1).withSpriteCount(1).withSheet(Image.TILESHEET).build();
    public static final Spritesheet SPRITESHEET = new SpritesheetBuilder().withColumns(2).withRows(2).withSpriteCount(4).withSheet(Image.SPRITESHEET).build();

    public static final ArrayList<Integer> COLLIDABLE_IDS = new ArrayList<>() {{
        add(1);
    }};
}
