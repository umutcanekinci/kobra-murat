package editor;

import java.awt.image.BufferedImage;

import common.Position;
import common.Object;

public class Tile extends Object{

    private int id;

    Tile(int id, int row, int column, BufferedImage image) {
        super(new Position(row, column));
        super.setImage(image);
        this.id = id;
    }

    public void setId(int id, BufferedImage image) {
        this.id = id;
        super.setImage(image);
    }

    public int getId() {
        return id;
    }
}
