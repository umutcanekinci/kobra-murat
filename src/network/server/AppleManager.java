package network.server;

import java.awt.Point;
import java.util.ArrayList;

import game.Utils;
import network.server.map.Tilemap;

class AppleManager {
    /*
     * This class is responsible for managing the apples in the game.
     * It is responsible for spawning apples and keeping track of them as points.
     * This script is seperated from the game.AppleManager class because server does not need to draw the apples.
     */

    public static final int APPLE_COUNT = 5;
    public static ArrayList<Point> apples = new ArrayList<>();

    public static Point spawnApple() {
        Point apple = getRandomPoint();

        while (apples.contains(apple)) {
            apple = getRandomPoint();
        }

        // Check if apple is on a snake
        boolean validPosition = false;
        while (!validPosition) {
            validPosition = true;
            for (NetPlayer player : PlayerList.players.values()) {
                if (player.snake.doesCollide(apple)) {
                    apple = getRandomPoint();
                    validPosition = false;
                    break;
                }
            }
        }

        while (Tilemap.getTile(apple).isCollidable()) {
            apple = getRandomPoint();
        }

        apples.add(apple);
        return apple;
    }

    public static Point getRandomPoint() {
        return Utils.getRandomPoint(Tilemap.getCols(), Tilemap.getRows());
    }

    public static void spawnApples() {
        for (int i = 0; i < APPLE_COUNT - apples.size(); i++) {
            spawnApple();
        }
    }

}