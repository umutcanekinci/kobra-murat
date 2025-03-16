package client;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import common.Utils;
import common.packet.apple.EatApplePacket;
import common.packet.apple.SpawnApplePacket;

public class AppleManager {

    private static final int APPLE_COUNT = 5;
    private static final ArrayList<Apple> apples = new ArrayList<>();
    private static ArrayList<Point> emptyTiles;

    public static String getInfo() {
        String str = "APPLES (" + apples.size() + ")\n";

        if(apples.isEmpty()) {
            return str + "No apples.\n";
        }

        for (Apple apple : apples) {
            str += apple + "\n";
        }

        return str;
    }

    public static void setEmptyTiles(ArrayList<Point> emptyTiles) {
        AppleManager.emptyTiles = emptyTiles;
    }

    public static void addApple(Point position) {
        apples.add(new Apple(position));
    }

    public static void addApple(SpawnApplePacket packet) {
        apples.add(new Apple(packet.position));
    }

    public static void spawnApples() {
        while(apples.size() < APPLE_COUNT) {
            if(!spawnApple())
                break;
        }
    }

    public static boolean spawnApple() {
        /*
         * Instead of getting a random point and checking if it collides with the snake or an apple,
         * we can get the arraylist of empty points and get a random point from there.
         * EMPTY_TILE_COUNT = TOTAL_TILES - TOTAL_COLLIDABLE_TILES - TOTAL_APPLES
         * We should not spawn an apple if EMPTY_TILE_COUNT <= TOTAL_SNAKE_SIZE + APPLE_COUNT
         * We have to remove the snake parts from the empty tiles every time we spawn an apple because the snake can grow and move
         */
        if(emptyTiles == null)
            return false;
        
        ArrayList<Point> spawnableTiles = new ArrayList<>(emptyTiles);
        spawnableTiles.removeAll(PlayerList.getSnakeParts()); 

        if(spawnableTiles.isEmpty())
            return false;
        
        Point position = Utils.getRandomPointFrom(spawnableTiles);
        addApple(position);
        emptyTiles.remove(position);
        return true;
    }

    public static void removeAll(ArrayList<Apple> applesToRemove) {
        apples.removeAll(applesToRemove);
    }

    public static ArrayList<Point> getCollecteds(NetPlayer player) {
        ArrayList<Point> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            //if(PlayerList.growIfCollide(apple.getPosition()))
            if(player.doesCollide(apple.getPosition()))
                collectedApples.add(apple.getPosition());
        }
        return collectedApples;
    }

    public static void clear() {
        apples.clear();
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        apples.forEach(apple -> apple.draw(g, observer));
    }

    public static void drawColliders(Graphics2D g) {
        apples.forEach(apple -> apple.drawCollider(g));
    }

    public static void remove(EatApplePacket packet) {
        apples.removeIf(apple -> apple.getPosition().equals(packet.position));
    }

}
