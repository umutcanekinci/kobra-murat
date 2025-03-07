package network.packet;
import game.Window;
import network.Connection;
import network.PlayerList;
import game.Board;

public class PacketHandler {

    private static boolean isServer = false;
    private static Board board;

    public static void init(Board board) {
        PacketHandler.board = board;
    }

    public static void setServer(boolean isServer) {
        PacketHandler.isServer = isServer;
    }

    public static void handle(Object packet, Connection connection) {

        int id = ((Packet) packet).id;

        if(packet instanceof SetIdPacket) {
            /*
                This will be invoked only once
                This is the first packet sent by the server to the client
                This packet is used by the client and server to set the id of the player
            */
            PlayerList.id = id;
            PlayerList.addPlayer(board, connection, id);
            board.initPlayer();
        }
        else if(packet instanceof AddPlayerPacket) {
            if(PlayerList.players.containsKey(id))
                return;
            PlayerList.addPlayer(board, connection, id);
        } 
        else if(packet instanceof RemovePlayerPacket) {
            PlayerList.removePlayer((RemovePlayerPacket) packet);
            sentToAllIfServer(packet);
        } else if(packet instanceof UpdatePlayerPack) {
            PlayerList.updatePlayerTransform((UpdatePlayerPack) packet);
            sentToAllIfServer(packet);
        }
        else if(packet instanceof ServerClosedPacket) {
            // This packet is sent by the server to the client when the server is closed
            // This packet is used by the client to close the connection
            connection.close();
            Window.exit();
        }

    }

    private static void sentToAllIfServer(Object data) {
        if(!isServer)
            return;

        PlayerList.sendToAll(data);
    }

    /*private handleServerPacket(Packet p) {
        // This method should be used by both server and client
        // Because server and client both are players and need to update the board

        
    }*/

}
