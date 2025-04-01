package client;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import common.Utils;
import common.packet.basic.SetMapPacket;
import common.Constants;
import common.Position;

public class Tilemap {
    
    private static Tile[][] tiles;
    private static ArrayList<Position> emptyTiles;
    private static int cols, rows;
    private static Position spawnPoint;
    private static int currentLevel;

    public static int getWidth() {
        return cols * common.Constants.TILE_SIZE;
    }

    public static int getHeight() {
        return rows * common.Constants.TILE_SIZE;
    }

    public static boolean isReady() {
        return tiles != null;
    }

    public static void load(SetMapPacket packet) {
        if(packet == null)
            throw new IllegalArgumentException("Packet cannot be null");

        currentLevel = packet.getId();
        load(currentLevel);
    }

    public static void load(int id) {
        File file = new File("maps/" + id + ".txt");

        Path path = Paths.get(file.getAbsolutePath());
        if(!Files.exists(path))
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " does not exist");
            
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
            throw new IllegalArgumentException("Data cannot be null");

        if(data.length == 0 || data[0].length == 0)
            throw new IllegalArgumentException("Data cannot be empty");
        
        cols = data[0].length;
        rows = data.length;
        tiles = new Tile[rows][cols];
        emptyTiles = new ArrayList<>();

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++)
            {
                Tile tile = new Tile(data[row][col], row, col, Constants.TILESHEET.getSprite(data[row][col]));
                tiles[row][col] = tile;

                if(tile.isSpawnPoint())
                    spawnPoint = tile.getPosition();

                if(!tile.isCollidable())
                    emptyTiles.add(tile.getPosition());
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
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null");

        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile.doesCollide(position))
                    return true;
            }
        }
        return false;
    }

    public static void draw(Graphics2D renderer, ImageObserver observer) {
        if(renderer == null)
            throw new IllegalArgumentException("Graphics2D cannot be null");
        
        if(tiles == null)
            return;
        
        for (Tile[] row : tiles) {
            for (Tile tile : row)
                tile.draw(renderer, observer);
        }
    }

    public static void drawColliders(Graphics2D renderer) {
        if(renderer == null)
            throw new IllegalArgumentException("Graphics2D cannot be null");

        if(tiles == null)
            return;

        for (Tile[] row : tiles) {
            for (Tile tile : row)
                tile.drawCollider(renderer);
        }
    }

    public static Tile getTile(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null");

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
