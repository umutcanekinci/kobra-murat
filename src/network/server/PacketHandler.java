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

public class PacketHandler {

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());
    private static Server server;

    public static void init(Server server) {
        PacketHandler.server = server;
    }

    public static void handle(Object packet, Connection connection) {
        LOGGER.log(Level.INFO, "Server received a packet: " + packet + "\n");
        
        switch (packet) {
            case EatApplePacket eatApplePacket -> {
                server.sendToAll(new SpawnApplePacket(AppleManager.spawnApple()));
            }
            case UpdateTransformPacket playerTransformPacket -> {
                AppleManager.updatePlayerTransform(playerTransformPacket);
                server.sendToAll(packet);
            }
            case StepPacket stepPacket -> {
                server.sendToAll(packet);
            }
            case DisconnectPacket disconnectPacket -> {
                server.closeConnection(connection);
                server.removeConnection(disconnectPacket.id);
                server.sendToAll(new RemovePacket(disconnectPacket.id));
            }
            default -> {
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
            }
        }
    }

}
