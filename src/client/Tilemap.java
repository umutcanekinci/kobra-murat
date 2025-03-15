package client;

import java.awt.*;
import java.awt.image.ImageObserver;

import common.Level;

public class Tilemap {

    private static int[][] data;
    private static Tile[][] tiles;
    private static int cols, rows;
    private static final Point spawnPoint = new Point(0, 0);
    private static int currentLevel;

    public static boolean isReady() {
        return tiles != null;
    }

    public static void load(int level) {
        currentLevel = level;
        int[][] mapData = Level.get(level);

        if(mapData == null)
            return;

        if(mapData.length == 0 || mapData[0].length == 0)
            return;

        data = mapData;
        cols = data[0].length;
        rows = data.length;
        tiles = new Tile[rows][cols];

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                tiles[row][col] = new Tile(data[row][col], row, col);

                if(data[row][col] == 2)
                    spawnPoint.setLocation(col, row);
            }
        }
    }

    public static int getCols() {
        return cols;
    }

    public static int getRows() {
        return rows;
    }

    public static Point getSpawnPoint() {
        return spawnPoint;
    }

    public static Tile getTile(Point position) {
        int row = position.y;
        int col = position.x;

        if(row < 0 || row >= rows || col < 0 || col >= cols)
            return null;

        return tiles[row][col];
    }

    public static boolean doesCollide(Point position) {
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
            for (Tile tile : row) {
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
            for (Tile tile : row) 
                tile.drawCollider(renderer);
        }
    }

    public static Point getRandomEmptyPoint() {
        Point point = new Point();
        do {
            point.setLocation((int) (Math.random() * cols), (int) (Math.random() * rows));
        } while (Tilemap.getTile(point).isCollidable());
        return point;
    }

    public static String getInfo() {
        return "Level " + currentLevel + " (" + cols + "x" + rows + ")" + "\n" +
        "Spawn Point: [" + spawnPoint.x + ", " + spawnPoint.y + "]";
    }

}
