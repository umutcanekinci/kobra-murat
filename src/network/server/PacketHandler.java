package network.server;
import network.Connection;
import network.packet.*;
import network.packet.client.RemovePacket;

public class PacketHandler {

    private static Server server;

    public static void init(Server server) {
        PacketHandler.server = server;
    }

    public static void handle(Object packet, Connection connection) {
        System.out.println("[CLIENT] " + packet);
    
        int id = ((Packet) packet).id;

        if(packet instanceof PlayerTransformPacket) {
            server.sendToAll(packet);
        }
        else if(packet instanceof DisconnectPacket) {
            server.closeConnection(connection);
            server.removeConnection(id);
            server.sendToAll(new RemovePacket(id));
        }
    }

}
