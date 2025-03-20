package server;

import java.util.ArrayList;

import common.Position;
import common.Connection;
import common.Level;
import common.packet.SetMapPacket;

public class Tilemap {

    static int currentLevel;
    private static int[][] data;
    private static Tile[][] tiles;
    private static ArrayList<Position> emptyTiles;
    private static int cols, rows;
    private static final Position spawnPoint = new Position(0, 0);

    public static void load(int level) {
        currentLevel = level;
        int[][] mapData = getLevel(level);

        if(mapData == null)
            return;

        if(mapData.length == 0 || mapData[0].length == 0)
            return;

        data = mapData;
        cols = data[0].length;
        rows = data.length;
        tiles = new Tile[rows][cols];
        emptyTiles = new ArrayList<>();
    
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                tiles[row][col] = new Tile(data[row][col], row, col);

                if(data[row][col] == 2)
                    spawnPoint.setLocation(col, row);

                if(data[row][col] == 0)
                    emptyTiles.add(new Position(col, row));
            }
        }
    }

    private static int[][] getLevel(int level) {
        return Level.get(level);
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

    public static Tile getTile(Position position) {
        int row = position.y;
        int col = position.x;
        if(row < 0 || row >= rows || col < 0 || col >= cols)
            return null;

        return tiles[row][col];
    }

    public static String getInfo() {
        return "Level " + currentLevel + " (" + cols + "x" + rows + ")" + "\n" +
        "Spawn Position: [" + spawnPoint.x + ", " + spawnPoint.y + "]";
    }

    public static void sendLevel(Connection connection) {
        connection.sendData(new SetMapPacket(Tilemap.currentLevel));
    }

    public static ArrayList<Position> getEmptyTiles() {
        return emptyTiles;
    }    

}
