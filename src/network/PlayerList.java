package network;
import java.util.HashMap;
import game.player.NetPlayer;
import network.packet.player.AddPacket;
import network.packet.player.UpdateTransformPacket;
import network.packet.player.RemovePacket;
import network.packet.player.StepPacket;

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

    public static String getDebugInfo() {
        String info = "PLAYER LIST: \n";

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
        players.put(id, player);
        players.get(id).connection = connection;
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
        player.move();
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
