package client;
import java.util.HashMap;

import common.packet.player.AddPacket;
import common.packet.player.RemovePacket;
import common.packet.player.StepPacket;
import common.packet.player.UpdateTransformPacket;
import common.Connection;

public class PlayerList {

    /*
     * This class is used to store the players in the game.
     * It is used by both the server and the client.
     * It has the following methods:
     * 1. addPlayer(Connection connection, AddPlayerPacket player)
     * 
     */
    public static int id;

    public static final HashMap<Integer, NetPlayer> players = new HashMap<>();

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

    public static void addPlayer(Connection connection, AddPacket player) {
        addPlayer(connection, player.id);
    }

    public static void addPlayer(Connection connection, int id) {
        NetPlayer player = new NetPlayer(id);
        player.setMap();
        player.connection = connection;
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
        for(NetPlayer player : players.values()) {
            if(player.connection == null)
                continue;

            player.connection.close();
        }
        players.clear();
    }

}
