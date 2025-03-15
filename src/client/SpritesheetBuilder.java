package client;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpritesheetBuilder {

    private BufferedImage spritesheet;
    private int rows, columns;
    private int spriteWidth, spriteHeight;
    private int spriteCount;

    public SpritesheetBuilder withSheet(BufferedImage img) {
        spritesheet = img;
        return this;
    }

    public SpritesheetBuilder withRows(int rows) {
        this.rows = rows;
        return this;
    }

    public SpritesheetBuilder withColumns(int columns) {
        this.columns = columns;
        return this;
    }

    public SpritesheetBuilder withSpriteSize(int width, int height) {
        this.spriteWidth = width;
        this.spriteHeight = height;
        return this;
    }

    public SpritesheetBuilder withSpriteCount(int count) {
        this.spriteCount = count;
        return this;
    }

    protected int getSpriteCount() {
        return spriteCount;
    }

    protected int getcolumns() {
        return columns;
    }

    protected int getRows() {
        return rows;
    }

    protected int getSpriteHeight() {
        return spriteHeight;
    }

    protected BufferedImage getSpritesheet() {
        return spritesheet;
    }

    protected int getSpriteWidth() {
        return spriteWidth;
    }

    public Spritesheet build() {
        int count = getSpriteCount();
        int rows = getRows();
        int columns = getcolumns();
        if (count == 0) {
            count = rows * columns;
        }

        BufferedImage sheet = getSpritesheet();

        int width = getSpriteWidth();
        int height = getSpriteHeight();
        if (width == 0) {
            width = sheet.getWidth() / columns;
        }
        if (height == 0) {
            height = sheet.getHeight() / rows;
        }

        int x = 0;
        int y = 0;
        List<BufferedImage> sprites = new ArrayList<>(count);

        for (int index = 0; index < count; index++) {
            sprites.add(sheet.getSubimage(x, y, width, height));
            x += width;
            if (x >= width * columns) {
                x = 0;
                y += height;
            }
        }

        return new Spritesheet(sprites);
    }
}