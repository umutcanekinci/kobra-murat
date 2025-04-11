package server;

import java.util.ArrayList;
import java.util.List;

import common.Connection;
import common.Utils;
import common.packet.SpawnApplesPacket;
import common.Position;

class AppleManager implements TilemapListener {
    /*
     * This class is responsible for managing the apples in the game.
     * It is responsible for spawning apples and keeping track of them as points.
     * This script is seperated from the game.AppleManager class because server does not need to draw the apples.
     */

    private static AppleManager instance;
    private static final int APPLE_COUNT = 5;
    private static ArrayList<Position> apples = new ArrayList<>();

    private AppleManager() {}

    public static AppleManager getInstance() {
        if(instance == null)
            instance = new AppleManager();

        return instance;
    }

    public void onMapLoaded(ArrayList<Position> emptyTiles) {
        spawnAll();
    }

    public static void addApple(Position position) {
        apples.add(position);
    }

    public static void spawnAll() {
        while(apples.size() < APPLE_COUNT) {
            if(spawn() == null)
                break;
        }
    }

    public static Position spawn() {
        /*
         * Instead of getting a random point and checking if it collides with the snake or an apple,
         * we can get the arraylist of empty points and get a random point from there.
         * EMPTY_TILE_COUNT = TOTAL_TILES - TOTAL_COLLIDABLE_TILES - TOTAL_APPLES
         * We should not spawn an apple if EMPTY_TILE_COUNT <= TOTAL_SNAKE_SIZE + APPLE_COUNT
         * We have to remove the snake parts from the empty tiles every time we spawn an apple because the snake can grow and move
         */
        List<Position> emptyTiles = Tilemap.getEmptyTiles();
        
        if(emptyTiles == null)
            return null;
        
        ArrayList<Position> spawnableTiles = new ArrayList<>(emptyTiles);
        spawnableTiles.removeAll(PlayerList.getSnakeParts()); 

        if(spawnableTiles.isEmpty())
            return null;
        
        Position position = Utils.getRandomPointFrom(spawnableTiles);
        addApple(position);
        emptyTiles.remove(position);
        return position;
    }

    public static List<Position> getCollecteds(NetPlayer player) {
        ArrayList<Position> collectedApples = new ArrayList<>();
        for (Position apple : apples) {
            //if(PlayerList.growIfCollide(apple.getPosition()))
            if(player.doesCollide(apple))
                collectedApples.add(apple);
        }
        return collectedApples;
    }

    public static void removeAll(List<Position> applesToRemove) {
        apples.removeAll(applesToRemove);
    }

    public static void sendAllTo(Connection connection) {
        connection.sendData(new SpawnApplesPacket(apples));
    }

    public static String getInfo() {
        StringBuilder str = new StringBuilder("APPLES (" + apples.size() + ")\n");

        if(apples.isEmpty()) 
            return str.append("No apples.\n").toString();

        apples.forEach(apple -> str.append(apple).append("\n"));

        return str.toString();
    }

}