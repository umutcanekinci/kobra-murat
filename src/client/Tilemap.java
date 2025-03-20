package client;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import common.Spritesheet;
import common.SpritesheetBuilder;
import common.Utils;
import common.Position;

public class Tilemap {
    
    private static Spritesheet TILE_SPRITESHEET;
    private static Tile[][] tiles;
    private static ArrayList<Position> emptyTiles;
    private static int cols, rows;
    private static Position spawnPoint;
    private static int currentLevel;

    public static void loadSheet() {
        TILE_SPRITESHEET = new SpritesheetBuilder().withColumns(1).withRows(1).withSpriteCount(1).withSheet(Utils.loadImage(new File("images/wall.png"))).build();
    }

    public static boolean isReady() {
        return tiles != null;
    }

    public static void load(int id) {
        File file = new File("maps/" + id + ".txt");

        Path path = Paths.get(file.getAbsolutePath());
        if(!Files.exists(path))
            return;
            
        String str = "";
        try {
            str = Files.readString(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        load(Utils.stringToData(str));
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
            {
                tiles[row][col] = new Tile(data[row][col], row, col, TILE_SPRITESHEET.getSprite(data[row][col]));

                if(tiles[row][col].isSpawnPoint)
                    spawnPoint = tiles[row][col].getPosition();
            }
                
        }
    }

    public static int getCols() {
        return cols;
    }

    public static int getRows() {
        return rows;
    }

    public static Position getSpawnPoint() {
        return spawnPoint;
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

    public static void draw(Graphics2D renderer, ImageObserver observer) {
        if(tiles == null)
            return;

        if(renderer == null)
            return;
        
        for (Tile[] row : tiles) {
            for (Tile tile : row)
                tile.draw(renderer, observer);
        }
    }

    public static void drawColliders(Graphics2D renderer) {
        if(tiles == null)
            return;

        if(renderer == null)
            return;

        for (Tile[] row : tiles) {
            for (Tile tile : row) 
                tile.drawCollider(renderer);
        }
    }

    public static Position getRandomEmptyPoint() {
        Position point = new Position();
        do {
            point.setLocation((int) (Math.random() * cols), (int) (Math.random() * rows));
        } while (Tilemap.getTile(point).isCollidable);
        return point;
    }

    public static Tile getTile(Position position) {
        int row = position.y;
        int col = position.x;

        if(row < 0 || row >= rows || col < 0 || col >= cols)
            return null;

        return tiles[row][col];
    }

    public static ArrayList<Position> getEmptyTiles() {
        return emptyTiles;
    }

    public static String getInfo() {
        return "Level " + currentLevel +
        " (" + cols + "x" + rows + ")" + "\n" +
        "Spawn Position: " + spawnPoint;
    }

}
