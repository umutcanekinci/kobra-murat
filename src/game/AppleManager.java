package game;
import java.awt.Point;
import java.util.ArrayList;

import game.player.NetPlayer;
import network.PlayerList;

public class AppleManager {

    public static final int APPLE_COUNT = 5;
    public static ArrayList<Point> apples = new ArrayList<>();
    public static int[][] mapData;

    private static Point getRandomPoint() {
        int minX = 0;
        int minY = 0;
        int maxX = mapData[0].length;
        int maxY = mapData.length;
        return new Point((int) (Math.random() * (maxX - minX) + minX), (int) (Math.random() * (maxY - minY) + minY));
    }

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
                if (player.snake.parts.contains(apple)) {
                    apple = getRandomPoint();
                    validPosition = false;
                    break;
                }
            }
        }

        while (mapData[apple.y][apple.x] == 1) {
            apple = getRandomPoint();
        }

        apples.add(apple);
        return apple;
    }

    public static void spawnApples() {
        for (int i = 0; i < APPLE_COUNT; i++) {
            spawnApple();
        }
    }

    public static void clear() {
        apples.clear();
    }

}
