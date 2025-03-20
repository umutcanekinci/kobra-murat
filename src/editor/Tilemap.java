package editor;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;

import common.Position;
import common.Level;
import common.Spritesheet;
import common.SpritesheetBuilder;
import common.Utils;

public class Tilemap {

    private static Spritesheet TILE_SPRITESHEET;
    private static Tile[][] tiles;
    private static int cols, rows;
    private static int currentLevel;

    public static void loadSheet() {
        TILE_SPRITESHEET = new SpritesheetBuilder().withColumns(1).withRows(1).withSpriteCount(1).withSheet(Utils.loadImage(new File("images/wall.png"))).build();
    }

    public static boolean isReady() {
        return tiles != null;
    }

    public static void newMap(int rows, int cols) {
        currentLevel = -1;
        int[][] data = new int[rows][cols];
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                data[row][col] = -1;
            }
        }
        load(data);
    }

    public static void load(int level) {
        currentLevel = level;
        int[][] data = Level.get(level);

        load(data);
    }

    public static void load(int[][] data) {
        if(data == null)
            return;

        if(data.length == 0 || data[0].length == 0)
            return;

        cols = data[0].length;
        rows = data.length;
        tiles = new Tile[rows][cols];

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Tile tile = new Tile(data[row][col], row, col, TILE_SPRITESHEET.getSprite(data[row][col]));
                tiles[row][col] = tile;
            }
        }
    }

    public static int[][] getData() {
        if(tiles == null)
            return null;

        int[][] data = new int[rows][cols];
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++)
                data[row][col] = tiles[row][col].getId();
        }
        return data;
    }

    public static int getCols() {
        return cols;
    }

    public static int getRows() {
        return rows;
    }

    public static Tile getTile(Position position) {
        int row = position.y;
        int col = position.x;

        if(row < 0 || row >= rows || col < 0 || col >= cols)
            return null;

        return tiles[row][col];
    }

    public static boolean doesCollide(Position position) {
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile.doesCollide(position))
                    return true;
            }
        }
        return false;
    }

    public static void changeTile(Position tile, int tileId) {
        if(tile == null)
            return;

        int row = tile.y;
        int col = tile.x;

        if(row < 0 || row >= rows || col < 0 || col >= cols)
            return;

        if(tiles[row][col] == null) {
            tiles[row][col] = new Tile(tileId, row, col, TILE_SPRITESHEET.getSprite(tileId));
            return;
        }

        tiles[row][col].setId(tileId, TILE_SPRITESHEET.getSprite(tileId));
    }

    public static void draw(Graphics2D renderer, ImageObserver observer) {
        if(tiles == null)
            return;

        if(renderer == null)
            return;
        
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if(tile == null)
                    continue;

                tile.draw(renderer, observer);
            }
        }
    }

    public static void drawColliders(Graphics2D renderer) {
        if(tiles == null)
            return;

        if(renderer == null)
            return;

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if(tile == null)
                    continue;

                tile.drawCollider(renderer);
            }
        }
    }

    public static String getInfo() {
        return "Level " + currentLevel + " (" + cols + "x" + rows + ")" + "\n";
    }

}
