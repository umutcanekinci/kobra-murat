package network;
import java.util.ArrayList;
import java.util.HashMap;
import game.Board;
import game.player.NetPlayer;
import network.packet.AddPlayerPacket;
import network.packet.RemovePlayerPacket;
import network.packet.UpdatePlayerPack;


public class PlayerList {

    /*
     * This class is used to store the players in the game.
     * It is used by both the server and the client.
     * It has following methods:
     * 1. addPlayer(Connection connection, AddPlayerPacket player)
     * 
     */
    public static int id;

    public static HashMap<Integer, NetPlayer> players = new HashMap<>();

    public static NetPlayer getCurrentPlayer() {
        return players.get(id);
    }

    public static boolean isCurrentPlayer(NetPlayer player) {
        return player.id == id;
    }

    public static String[] getDebugInfo() {
        ArrayList<String> debugInfo = new ArrayList<>();
        debugInfo.add("");
        if(players.isEmpty()) {
            debugInfo.add("No players.");
            debugInfo.add("");
            return debugInfo.toArray(new String[0]);
        }
    
        debugInfo.add("PLAYER LIST:");
        for (NetPlayer player: players.values()) {
            player.getDebugInfo();
            for (String s : player.getDebugInfo()) {
                debugInfo.add(s);
            }
            debugInfo.add("");
        }
        return debugInfo.toArray(new String[0]);
    }

    public static void addPlayer(Board board, Connection connection, AddPlayerPacket player) {
        addPlayer(board, connection, player.id);
    }

    public static void addPlayer(Board board, Connection connection, int id) {
        players.put(id, new NetPlayer(board, id));
        players.get(id).connection = connection;
    }

    public static void removePlayer(RemovePlayerPacket packet) {
        NetPlayer player = players.get(packet.id);
        System.out.println("Player " + packet.id + " is removed.");
        if(player == null)
            return;
            
        if(player.connection != null) {
            try {
                player.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        players.remove(packet.id);
    }

    public static void updatePlayerTransform(UpdatePlayerPack pack) {
        players.get(pack.id).update(pack);
        System.out.println("Position of Player " + pack.id + " is updated to " + pack.snake.getPosition() + ".");
        System.out.println("Direction of Player " + pack.id + " is updated to " + pack.snake.direction + ".");
    }
    
    public static void sendToAll(Object data) {
        for(NetPlayer player : players.values()) {
            if(player.connection == null)
                continue;
            player.connection.sendData(data);
        }
    }

    public static void clear() {
        for(NetPlayer player : players.values()) {
            if(player.connection == null)
                continue;
            try {
                player.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        players.clear();
    }

}
