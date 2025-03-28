package common;

import java.awt.Dimension;
import java.awt.Toolkit;

import common.graphics.Image;

public class Constants {

    /* 
     * There will be all the constants that will be used in different classes.
     * Class specified constants will be in the class itself, but the general ones will be here.
     */

    public static final int PORT = 7777;

    public static final int SPAWN_TILE = -2;    
    public static final Position HIDDEN_POSITION = new Position(-1, -1);

    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    public static final int TILE_SIZE = 64;
    public static final Dimension SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    public static final int DEFAULT_LENGTH = 12;

    public static final Spritesheet TILESHEET   = new SpritesheetBuilder().withColumns(1).withRows(1).withSpriteCount(1).withSheet(Image.TILESHEET).build();
    public static final Spritesheet SPRITESHEET = new SpritesheetBuilder().withColumns(3).withRows(3).withSpriteCount(9).withSheet(Image.SPRITESHEET).build();

}
