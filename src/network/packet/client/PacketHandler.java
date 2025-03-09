package network.packet.client;
import network.Connection;
import network.PlayerList;
import network.packet.Packet;
import network.packet.PlayerTransformPacket;
import network.client.Client;
import game.Board;

public class PacketHandler {

    private static Board board;
    private static Client client;

    public static void init(Board board, Client client) {
        PacketHandler.client = client;
        PacketHandler.board = board;
    }

    public static void handle(Object packet, Connection connection) {

        System.out.println("[SERVER] " + packet);

        int id = ((Packet) packet).id;

        if(packet instanceof IdPacket) {
            PlayerList.id = id;
            board.initPlayer();
        }
        else if(packet instanceof AddPacket) {
            if(PlayerList.players.containsKey(id))
                return;

            PlayerList.addPlayer(board, connection, (AddPacket) packet);
        } 
        else if(packet instanceof PlayerTransformPacket) {
            PlayerList.updatePlayerTransform((PlayerTransformPacket) packet);
        }
        else if(packet instanceof RemovePacket) {
            PlayerList.removePlayer((RemovePacket) packet);
        }
        else if(packet instanceof ServerClosedPacket) {
            client.close();
            board.openMenu();
        }
    }

}
