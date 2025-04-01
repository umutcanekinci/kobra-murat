package client;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;

import common.Direction;
import common.Position;

public class PlayerList {

    /*
     * This class is used to store the players in the game.
     * It is used by both the server and the client.
     * It has the following methods:
     * 1. addPlayer(Connection connection, AddPlayerPacket player)
     * 
     */
    private static int id;
    private static final HashMap<Integer, NetPlayer> players = new HashMap<>();

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        PlayerList.id = id;

        if(!Client.isConnected())
            OfflinePlayerController.setPlayer(getCurrentPlayer());
    }

    public static NetPlayer getCurrentPlayer() {
        return getPlayer(id);
    }

    private static NetPlayer getPlayer(int id) {
        if(!players.containsKey(id))
            throw new IllegalArgumentException("Player with id " + id + " does not exist");
        
        NetPlayer player = players.get(id);

        if(player == null)
            throw new IllegalArgumentException("Player with id " + id + " does not exist");

        return player;
    }

    public static ArrayList<NetPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public static int getPlayerCount() {
        return players.size();
    }

    public static ArrayList<Position> getSnakeParts() {
        ArrayList<Position> parts = new ArrayList<>();
        for (NetPlayer player : players.values()) {
            parts.addAll(player.getParts());
        }
        return parts;
    }

    public static void clear() {
        players.clear();
    }

    //region Single Player Methods

    public static void add(int id) {
        if (PlayerList.players.containsKey(id))
            return;

        players.put(id, new NetPlayer(id));
    }

    public static void spawn(int id, Position spawnPoint, int length) {
        if(spawnPoint == null)
            throw new IllegalArgumentException("Spawn point cannot be null");

        getPlayer(id).spawn(spawnPoint, length);
    }

    public static void updateTransform(int id, Direction direction, ArrayList<Position> parts, int tailIndex) {
        if(parts == null)
            throw new IllegalArgumentException("Parts cannot be null");

        if(direction == null)
            throw new IllegalArgumentException("Direction cannot be null");
        
        NetPlayer player = players.get(id);

        player.setDirection(direction);
        player.setParts(parts);
        player.setTailIndex(tailIndex);
    }

    public static void step(int id, Direction direction) {
        if(direction == null)
            throw new IllegalArgumentException("Direction cannot be null");

        NetPlayer player = players.get(id); 
        player.setDirection(direction);
        player.stepTo(player.getNextPosition());
    }

    public static void rotate(int id, Direction direction) {
        if(direction == null)
            throw new IllegalArgumentException("Direction cannot be null");

        NetPlayer player = players.get(id);
        player.setDirection(direction);
    }

    public static void grow(int id, int amount) {
        NetPlayer player = players.get(id);
        player.grow(amount);
    }

    public static void remove(int id) {
        NetPlayer player = players.get(id);
        if(player == null)
            return;
        players.remove(id);
    }

    //endregion

    public static boolean doesCollide(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null");

        for (NetPlayer player : players.values()) {
            if (player.doesCollide(position))
                return true;
        }
        return false;
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        if(players.isEmpty())
            return;

        players.values().forEach(p -> p.draw(g, observer));
    }

    public static void drawColliders(Graphics2D g) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        if(players.isEmpty())
            return;

        players.values().forEach(p -> p.drawCollider(g));
    }

    public static String getInfo() {
        StringBuilder str = new StringBuilder("PLAYERS (" + players.size() + ")\n");

        if(players.isEmpty())
            str.append("No players\n");
        else
            players.values().forEach(player -> str.append(player).append("\n"));

        return str.toString();
    }

}
