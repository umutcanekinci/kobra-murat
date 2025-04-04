package server;

import java.util.logging.Level;
import java.util.logging.Logger;

import common.packet.RotatePacket;
import common.packet.basic.DisconnectPacket;
import common.packet.basic.RemovePacket;
import common.Connection;

public class PacketHandler {
    
    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());
    
    public static void handle(Object packet, Connection connection) {
        System.out.println("Packet: " + packet);
        LOGGER.log(Level.INFO, packet + "\n");
        
        switch (packet) {
            case DisconnectPacket disconnectPacket -> {
                PlayerList.removePlayer(disconnectPacket);
                Server.closeConnection(connection);
                Server.removeConnection(disconnectPacket.getId());
                PlayerList.sendToAll(new RemovePacket(disconnectPacket.getId()));
            }
            
            case RotatePacket rotatePacket -> {
                PlayerList.players.get(rotatePacket.getId()).setDirection(rotatePacket.direction);
                PlayerList.sendToAll(packet);
            }

            default -> 
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }
}
