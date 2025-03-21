package editor;

import java.awt.*;
import java.awt.image.ImageObserver;

import common.Position;
import common.Spritesheet;
public class Tilemap {

    private static Spritesheet TILE_SPRITESHEET;
    private static Tile[][] tiles;
    private static int cols, rows;

    public static void newMap(int rows, int cols) {
        int[][] data = new int[rows][cols];
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                data[row][col] = -1;
            }
        }
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
            for(int col = 0; col < cols; col++)
                tiles[row][col] = new Tile(data[row][col], row, col, TILE_SPRITESHEET.getSprite(data[row][col]));
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
}
