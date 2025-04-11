package client;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import common.Utils;
import common.Position;

/**
 * The AppleManager class is responsible for managing the apples in the game.
 * It handles the spawning, removal, and drawing of apples on the game screen.
 * It also implements the ClientListener interface to handle client connection events.
 * @since 1.0
 * @see ClientListener
 * @see Apple
 * @see Position
 */
public class AppleManager implements ClientListener {

    private static AppleManager instance;
    private static final int APPLE_COUNT = 5;
    private static final ArrayList<Apple> apples = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     * @since 1.0
     */
    private AppleManager() {}

    /**
     * @return The singleton instance of the AppleManager class.
     * @since 1.0
     */
    public static AppleManager getInstance() {
        if(instance == null)
            instance = new AppleManager();

        return instance;
    }

    /**
     * Adds an apple to the list of apples.
     * @param position The position of the apple to be added.
     * @throws IllegalArgumentException if the position is null.
     * @since 1.0
     */
    public static void add(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null.");

        apples.add(new Apple(position));
    }

    /**
     * Adds a list of apples to the list of apples.
     * @param positions The list of positions of the apples to be added.
     * @throws IllegalArgumentException if the positions are null.
     * @since 1.0
     */
    public static void addAll(List<Position> positions) {
        if(positions == null)
            throw new IllegalArgumentException("Positions cannot be null.");

        apples.clear();
        positions.forEach(AppleManager::add);
    }

    /**
     * Spawns apples until the maximum number of apples is reached.
     * @since 1.0
     */
    public static void spawnAll() {
        apples.clear();
        while(apples.size() < APPLE_COUNT) {
            if(!spawnAtRandom())
                break;
        }
    }

    /**
     * Spawns a single apple at a random position.
     * If there is no empty tile, it will not spawn an apple.
     * @return true if the apple was spawned successfully, false otherwise.
     * @since 1.0
     */
    public static boolean spawnAtRandom() {
        /*
         * Instead of getting a random point and checking if it collides with the snake or an apple,
         * we can get the arraylist of empty points and get a random point from there.
         * EMPTY_TILE_COUNT = TOTAL_TILES - TOTAL_COLLIDABLE_TILES - TOTAL_APPLES
         * We should not spawn an apple if EMPTY_TILE_COUNT <= TOTAL_SNAKE_SIZE + APPLE_COUNT
         * We have to remove the snake parts from the empty tiles every time we spawn an apple because the snake can grow and move
         */
        ArrayList<Position> emptyTiles = Tilemap.getEmptyTiles();
        
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

    /**
     * Removes an apple from the list of apples.
     * @param position The position of the apple to be removed.
     * @throws IllegalArgumentException if the position is null.
     * @since 1.0
     */
    public static void remove(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null.");

        apples.removeIf(apple -> apple.doesCollide(position));
    }

    /**
     * Gets the list of positions of apples that were collected at the given position.
     * @param position The position of the player.
     * @return A list of positions of apples that were collected at the given position.
     */
    public static List<Position> getCollides(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null.");

        ArrayList<Position> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            if(apple.doesCollide(position))
                collectedApples.add(apple.getPosition());
        }
        return collectedApples;
    }

    /**
     * Clears the list of apples when the client connects.
     * @since 1.0
     */
    @Override
    public void onClientConnected() {
        apples.clear();
    }

    /**
     * Clears the list of apples when the client disconnects.
     * @since 1.0
     * @see ClientListener#onClientDisconnected()
     */
    @Override
    public void onClientDisconnected() {
        apples.clear();
    }

    /**
     * Draws the apples on the game screen.
     * @param g The graphics object used to draw the apples.
     * @param observer The image observer used to observe the images.
     * @throws IllegalArgumentException if the graphics object is null.
     * @since 1.0
     */
    public static void drawAll(Graphics2D g, ImageObserver observer) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null.");

        apples.forEach(apple -> apple.draw(g, observer));
    }

    /**
     * Gets the debug information of the apples.
     * @return A string containing the debug information of the apples.
     * @since 1.0
     */
    public static String getInfo() {
        StringBuilder str = new StringBuilder("APPLES (" + apples.size() + ")\n");

        if(apples.isEmpty()) 
            return str.append("No apples.\n").toString();

        apples.forEach(apple -> str.append(apple).append("\n"));

        return str.toString();
    }
}
