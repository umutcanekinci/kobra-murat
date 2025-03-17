package server;

import java.awt.Point;
import java.util.ArrayList;
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
            case StepPacket stepPacket -> {
                PlayerList.playerStep(stepPacket);
                PlayerList.sendToAll(packet);
                collectApples(PlayerList.players.get(stepPacket.id));
            }

            case DisconnectPacket disconnectPacket -> {
                PlayerList.removePlayer(disconnectPacket);
                Server.closeConnection(connection);
                Server.removeConnection(disconnectPacket.id);
                PlayerList.sendToAll(new RemovePacket(disconnectPacket.id));
            }
            
            default -> 
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }

    private static void collectApples(NetPlayer player) {
        ArrayList<Point> collectedApples = AppleManager.getCollecteds(player);

        if(collectedApples.isEmpty())
            return;

        AppleManager.removeAll(collectedApples);
        collectedApples.forEach(apple -> PlayerList.sendToAll(new EatApplePacket(player.getId(), apple)));
        collectedApples.forEach(apple -> PlayerList.sendToAll(new SpawnApplePacket(AppleManager.spawn())));
        player.grow(collectedApples.size());
    }

}
