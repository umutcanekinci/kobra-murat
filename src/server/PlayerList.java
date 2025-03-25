package server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import common.packet.SpawnPacket;
import common.packet.StepPacket;
import common.packet.UpdateTransformPacket;
import common.packet.basic.AddPacket;
import common.packet.basic.DisconnectPacket;
import common.Connection;

public class PlayerList {

    /*
     * This class is used to store the players in the game.
     * It is used by both the server and the client.
     * It has the following methods:
     * 1. addPlayer(Connection connection, AddPlayerPacket player)
     * 
     */
    public static final HashMap<Integer, NetPlayer> players = new HashMap<>();

    public static int size() {
        return players.size();
    }

    public static void addPlayer(Connection connection, int id) {
        NetPlayer player = new NetPlayer(connection, id);
        players.put(id, player);
    }

    public static void spawnPlayer(SpawnPacket packet) {
        NetPlayer player = players.get(packet.getId());
        player.spawn(packet.getSpawnPoint());
    }

    public static void removePlayer(DisconnectPacket packet) {
        NetPlayer player = players.get(packet.getId());
        
        if(player == null)
            return;
            
        players.remove(packet.getId());
    }

    public static void playerStep(StepPacket packet) {
        NetPlayer player = players.get(packet.getId());
        
        if(player == null)
            return;
        
        player.setDirection(packet.direction);
        player.stepTo(player.getNextPosition());
    }

    public static void clear() {
        players.values().forEach(NetPlayer::close);
        players.clear();
    }

    public static ArrayList<Point> getSnakeParts() {
        ArrayList<Point> parts = new ArrayList<>();
        for (NetPlayer player : players.values()) {
            parts.addAll(player.getParts());
        }
        return parts;
    }

    public static void sendAllTo(Connection connection) {
        players.forEach((key, value) -> connection.sendData(new AddPacket(key)));
        players.forEach((key, value) -> connection.sendData(new UpdateTransformPacket(value)));
    }

    public static void sendToAll(Object packet) {
        players.values().forEach((player) -> player.send(packet));
    }

    public static String getInfo() {
        StringBuilder str = new StringBuilder("PLAYERS (" + players.size() + ")\n");

        if(players.isEmpty())
            str.append("No players\n");
        else
            players.values().forEach((player) -> str.append(player).append("\n"));

        return str.toString();
    }

}
