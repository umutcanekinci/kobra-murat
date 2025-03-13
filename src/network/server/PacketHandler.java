package network.server;
import network.Connection;
import network.packet.apple.EatApplePacket;
import network.packet.apple.SpawnApplePacket;
import network.packet.player.DisconnectPacket;
import network.packet.player.UpdateTransformPacket;
import network.packet.player.RemovePacket;
import network.packet.player.StepPacket;

import java.util.logging.Level;
import java.util.logging.Logger;

import game.AppleManager;

public class PacketHandler {

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());
    public static void handle(Object packet, Connection connection) {
        //LOGGER.log(Level.INFO, "Server received a packet: " + packet + "\n");
        
        switch (packet) {
            case EatApplePacket eatApplePacket -> {
                Server.sendToAll(new SpawnApplePacket(AppleManager.spawnApple()));
            }
            case UpdateTransformPacket playerTransformPacket -> {
                AppleManager.updatePlayerTransform(playerTransformPacket);
                Server.sendToAll(packet);
            }
            case StepPacket stepPacket -> {
                Server.sendToAll(packet);
            }
            case DisconnectPacket disconnectPacket -> {
                Server.closeConnection(connection);
                Server.removeConnection(disconnectPacket.id);
                Server.sendToAll(new RemovePacket(disconnectPacket.id));
            }
            default -> {
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
            }
        }
    }

}
