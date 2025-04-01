package client;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import common.Utils;
import common.Position;

public class AppleManager {

    private static final int APPLE_COUNT = 5;
    private static final ArrayList<Apple> apples = new ArrayList<>();
    private static ArrayList<Position> emptyTiles;

    public static void setEmptyTiles(ArrayList<Position> emptyTiles) {
        if(emptyTiles == null)
            throw new IllegalArgumentException("Empty tiles cannot be null.");

        AppleManager.emptyTiles = emptyTiles;
    }

    public static void add(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null.");

        apples.add(new Apple(position));
    }

    public static void addAll(ArrayList<Position> positions) {
        if(positions == null)
            throw new IllegalArgumentException("Positions cannot be null.");

        apples.clear();
        positions.forEach(AppleManager::add);
    }

    public static void spawnAll() {
        apples.clear();
        while(apples.size() < APPLE_COUNT) {
            if(!spawn())
                break;
        }
    }

    public static boolean spawn() {
        /*
         * Instead of getting a random point and checking if it collides with the snake or an apple,
         * we can get the arraylist of empty points and get a random point from there.
         * EMPTY_TILE_COUNT = TOTAL_TILES - TOTAL_COLLIDABLE_TILES - TOTAL_APPLES
         * We should not spawn an apple if EMPTY_TILE_COUNT <= TOTAL_SNAKE_SIZE + APPLE_COUNT
         * We have to remove the snake parts from the empty tiles every time we spawn an apple because the snake can grow and move
         */
        if(emptyTiles == null)
            return false;
        
        ArrayList<Position> spawnableTiles = new ArrayList<>(emptyTiles);
        spawnableTiles.removeAll(PlayerList.getSnakeParts()); 

        if(spawnableTiles.isEmpty())
            return false;
        
        Position position = Utils.getRandomPointFrom(spawnableTiles);
        add(position);
        emptyTiles.remove(position);
        return true;
    }

    public static void remove(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null.");

        apples.removeIf(apple -> apple.doesCollide(position));
    }

    public static ArrayList<Position> getCollecteds(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null.");

        ArrayList<Position> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            if(apple.doesCollide(position))
                collectedApples.add(apple.getPosition());
        }
        return collectedApples;
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null.");

        apples.forEach(apple -> apple.draw(g, observer));
    }

    public static void drawColliders(Graphics2D g) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null.");

        apples.forEach(apple -> apple.drawCollider(g));
    }

    public static String getInfo() {
        StringBuilder str = new StringBuilder("APPLES (" + apples.size() + ")\n");

        if(apples.isEmpty()) 
            return str.append("No apples.\n").toString();

        apples.forEach(apple -> str.append(apple).append("\n"));

        return str.toString();
    }

}
