package common;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import common.graphics.image.Image;

//https://stackoverflow.com/questions/35472233/load-a-sprites-image-in-java

public class Spritesheet {

    public static final Spritesheet TILESHEET   = new SpritesheetBuilder().withColumns(1).withRows(1).withSpriteCount(1).withSheet(Image.TILESHEET).build();
    public static final Spritesheet SNAKESHEET = new SpritesheetBuilder().withColumns(2).withRows(2).withSpriteCount(4).withSheet(Image.SPRITESHEET).build();

    private final List<BufferedImage> sprites;

    public Spritesheet(List<BufferedImage> sprites) {
        this.sprites = new ArrayList<>(sprites);
    }

    public int count() {
        return sprites.size();
    }

    public BufferedImage getSprite(int frame) {
        if(frame < 0 || frame >= sprites.size())
            return null;

        if(frame < 0 || frame >= sprites.size())
            return null;
            
        return sprites.get(frame);
    }

}