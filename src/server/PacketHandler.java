package server;

import java.util.logging.Level;
import java.util.logging.Logger;

import common.packet.apple.EatApplePacket;
import common.packet.apple.SpawnApplePacket;
import common.packet.player.DisconnectPacket;
import common.packet.player.RemovePacket;
import common.packet.player.StepPacket;
import common.Connection;

public class PacketHandler {
    
    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());
    public static void handle(Object packet, Connection connection) {
        LOGGER.log(Level.INFO, packet + "\n");
        
        switch (packet) {
            case EatApplePacket eatApplePacket -> 
                Server.sendToAll(new SpawnApplePacket(AppleManager.spawnApple()));
            
            case StepPacket stepPacket -> {
                PlayerList.playerStep(stepPacket);
                Server.sendToAll(packet);
            }

            case DisconnectPacket disconnectPacket -> {
                PlayerList.removePlayer(disconnectPacket);
                Server.closeConnection(connection);
                Server.removeConnection(disconnectPacket.id);
                Server.sendToAll(new RemovePacket(disconnectPacket.id));
            }
            
            default -> 
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }

}
