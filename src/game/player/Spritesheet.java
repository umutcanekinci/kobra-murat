package game.player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Spritesheet {

    private final List<BufferedImage> sprites;

    public Spritesheet(List<BufferedImage> sprites) {
        this.sprites = new ArrayList<>(sprites);
    }

    public int count() {
        return sprites.size();
    }

    public BufferedImage getSprite(double progress) {
        int frame = (int) (count() * progress);
        return sprites.get(frame);
    }

    public BufferedImage getSprite(int frame) {
        return sprites.get(frame);
    }

}