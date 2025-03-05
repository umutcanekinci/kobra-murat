package network;

import network.server.NetPlayer;
import packet.AddPlayerPacket;
import packet.RemovePlayerPacket;
import packet.UpdatePlayerPack;

import java.util.HashMap;

public class PlayerHandler {

    public static HashMap<Integer, NetPlayer> players = new HashMap<>();

    public static void addPlayer(Connection connection, int id, String name) {
        players.put(id, new NetPlayer(id, name));
        players.get(id).connection = connection;
        System.out.println(name + " ("+id+") has joined to the game.");
    }

    public static void addPlayer(Connection connection, AddPlayerPacket player) {
        addPlayer(connection, player.id, player.name);
    }

    public static void removePlayer(RemovePlayerPacket player) {
        System.out.println(PlayerHandler.players.get(player.id).name + " has left the game.");
        players.remove(player.id);
    }

    public static void updatePlayerTransform(UpdatePlayerPack pack) {
        players.get(pack.id).update(pack);
        System.out.println("Position of Player " + pack.id + " is updated to " + pack.snake.getPosition() + ".");
        System.out.println("Direction of Player " + pack.id + " is updated to " + pack.snake.direction + ".");
    }
}
