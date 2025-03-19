package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import common.packet.player.DisconnectPacket;
import common.packet.player.RemovePacket;
import common.packet.player.RotatePacket;
import common.Connection;

public class PacketHandler {
    
    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());
    public static void handle(Object packet, Connection connection) {
        LOGGER.log(Level.INFO, packet + "\n");
        
        switch (packet) {
            case DisconnectPacket disconnectPacket -> {
                PlayerList.removePlayer(disconnectPacket);
                Server.closeConnection(connection);
                Server.removeConnection(disconnectPacket.id);
                PlayerList.sendToAll(new RemovePacket(disconnectPacket.id));
            }
            
            case RotatePacket rotatePacket -> {
                PlayerList.players.get(rotatePacket.id).setDirection(rotatePacket.direction);
                PlayerList.sendToAll(packet);
            }

            default -> 
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }
}
