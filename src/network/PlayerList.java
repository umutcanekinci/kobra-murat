package network;

import network.server.NetPlayer;
import packet.AddPlayerPacket;
import packet.RemovePlayerPacket;
import packet.UpdatePlayerPack;
import java.util.HashMap;


public class PlayerList {

    /*
     * This class is used to store the players in the game.
     * It is used by both the server and the client.
     * It has following methods:
     * 1. addPlayer(Connection connection, AddPlayerPacket player)
     * 
     */

    public static HashMap<Integer, NetPlayer> players = new HashMap<>();

    public static void addPlayer(Connection connection, AddPlayerPacket player) {
        addPlayer(connection, player.id);
    }

    public static void addPlayer(Connection connection, int id) {
        players.put(id, new NetPlayer(id));
        players.get(id).connection = connection;
    }

    public static void removePlayer(RemovePlayerPacket player) {
        players.remove(player.id);
    }

    public static void updatePlayerTransform(UpdatePlayerPack pack) {
        players.get(pack.id).update(pack);
        System.out.println("Position of Player " + pack.id + " is updated to " + pack.snake.getPosition() + ".");
        System.out.println("Direction of Player " + pack.id + " is updated to " + pack.snake.direction + ".");
    }

}
