package server;

import java.util.ArrayList;

import common.Connection;
import common.Utils;
import common.packet.SpawnApplePacket;
import common.Position;

class AppleManager {
    /*
     * This class is responsible for managing the apples in the game.
     * It is responsible for spawning apples and keeping track of them as points.
     * This script is seperated from the game.AppleManager class because server does not need to draw the apples.
     */

    private static final int APPLE_COUNT = 5;
    private static ArrayList<Position> apples = new ArrayList<>();
    private static ArrayList<Position> emptyTiles;

    public static String getInfo() {
        String str = "APPLES (" + apples.size() + ")\n";

        if(apples.isEmpty()) {
            return str + "No apples.\n";
        }

        for (Position apple : apples) {
            str += apple;
        }

        return str;
    }

    public static void setEmptyTiles(ArrayList<Position> emptyTiles) {
        AppleManager.emptyTiles = emptyTiles;
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

    public static ArrayList<Position> getCollecteds(NetPlayer player) {
        ArrayList<Position> collectedApples = new ArrayList<>();
        for (Position apple : apples) {
            //if(PlayerList.growIfCollide(apple.getPosition()))
            if(player.doesCollide(apple))
                collectedApples.add(apple);
        }
        return collectedApples;
    }

    public static void removeAll(ArrayList<Position> applesToRemove) {
        apples.removeAll(applesToRemove);
    }

    public static void sendAllTo(Connection connection) {
        apples.forEach((apple) -> connection.sendData(new SpawnApplePacket(apple)));
    }

}