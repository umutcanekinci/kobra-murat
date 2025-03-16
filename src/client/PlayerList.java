package client;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;

import common.packet.player.AddPacket;
import common.packet.player.RemovePacket;
import common.packet.player.StepPacket;
import common.packet.player.UpdateTransformPacket;

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
    }

    public static NetPlayer getCurrentPlayer() {
        return players.get(id);
    }

    public static String getInfo() {
        String info = "PLAYERS (" + players.size() + ") \n";

        if(players.isEmpty()) {
            info += "No players.\n";
            return info;
        }

        for (NetPlayer player: players.values()) {
            info += player + "\n";
        }
        return info;
    }

    public static ArrayList<NetPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public static int getPlayerCount() {
        return players.size();
    }

    public static ArrayList<Point> getSnakeParts() {
        ArrayList<Point> parts = new ArrayList<>();
        for (NetPlayer player : players.values()) {
            parts.addAll(player.snake.getParts());
        }
        return parts;
    }

    public static void addPlayer(AddPacket player) {
        if (PlayerList.players.containsKey(player.id))
            return;

        addPlayer(player.id);
    }

    public static void addPlayer(int id) {
        NetPlayer player = new NetPlayer(id);
        player.setMap();
        player.reset();
        players.put(id, player);
    }

    public static void removePlayer(RemovePacket packet) {
        NetPlayer player = players.get(packet.id);
        if(player == null)
            return;
        players.remove(packet.id);
    }

    public static void updatePlayerTransform(UpdateTransformPacket packet) {
        NetPlayer player = players.get(packet.id);
        if(player == null)
            return;
        player.snake.setDirection(packet.direction);
        player.snake.setParts(packet.parts);
        player.getPos().setLocation(packet.position);
        player.snake.rotateHeadTransform();
    }

    public static void playerStep(StepPacket packet) {
        NetPlayer player = players.get(packet.id);
        if(player == null)
            return;
        player.snake.setDirection(packet.direction);
        player.canRotate = true;
        player.displacement = 0;
        player.snake.step();
    }

    public static void clear() {
        players.clear();
    }

    public static boolean doesCollide(Point position) {
        for (NetPlayer player : players.values()) {
            if (player.snake.doesCollide(position)) {
                return true;
            }
        }
        return false;
    }

    public static boolean growIfCollide(Point position) {
        for (NetPlayer player : players.values()) {
            if (player.snake.doesCollide(position)) {
                player.snake.grow(1);
                return true;
            }
        }
        return false;
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        if(g == null || players.isEmpty())
            return;

        players.values().forEach(p -> p.snake.draw(g, observer));
    }

    public static void drawColliders(Graphics2D g) {
        if(g == null || players.isEmpty())
            return;

        players.values().forEach(p -> p.snake.drawCollider(g));
    }

}
