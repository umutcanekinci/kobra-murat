package server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import common.Position;
import common.ServerListener;
import common.Utils;
import common.packet.basic.SetMapPacket;
import common.Connection;

public class Tilemap implements ServerListener {

    /*
     * This class is responsible for loading the map from a file and keeping track of the tiles.
     * It is also responsible for checking if a position collides with a tile.
     * This class is a singleton because we only need one instance of it.
     */

    private static Tilemap INSTANCE;
    private static int currentLevel;
    private static Tile[][] tiles;
    private static ArrayList<Position> emptyTiles;
    private static int cols, rows;
    private static final Position spawnPoint = new Position(0, 0);
    private static ArrayList<TilemapListener> listeners = new ArrayList<>();

    public static void addListener(TilemapListener listener) {
        if(listener == null)
            throw new IllegalArgumentException("Listener cannot be null");
        
        listeners.add(listener);
    }

    private Tilemap() {}

    public static Tilemap getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Tilemap();

        return INSTANCE;
    }

    @Override
    public void onServerConnected(String ip) {
        load(1);
    }

    @Override
    public void onServerClosed() {
    }

    @Override
    public void onServerStartedGame() {
    }

    public static void load(int id) {
        currentLevel = id;
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
        emptyTiles = new ArrayList<>();
    
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Tile tile = new Tile(data[row][col], row, col);
                tiles[row][col] = tile;

                if(tile.isSpawnPoint())
                    spawnPoint.setLocation(tile);

                if(!tile.isCollidable())
                    emptyTiles.add(new Position(tile));
            }
        }
        listeners.forEach(listener -> listener.onMapLoaded(emptyTiles));
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

    public static void sendMap(Connection connection) {
        connection.sendData(new SetMapPacket(Tilemap.currentLevel));
    }

    public static ArrayList<Position> getEmptyTiles() {
        return emptyTiles;
    }

    public static String getInfo() {
        return "Level " + currentLevel + " (" + cols + "x" + rows + ")" + "\n" +
        "Spawn Position: " + spawnPoint + "\n";
    }
}
