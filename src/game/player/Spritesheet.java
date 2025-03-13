package game.player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

//https://stackoverflow.com/questions/35472233/load-a-sprites-image-in-java

public class Spritesheet {

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
            
        return sprites.get(frame);
    }

}