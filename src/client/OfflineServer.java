package client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.packet.SetMapPacket;
import common.packet.apple.EatApplePacket;
import common.packet.player.AddPacket;
import common.packet.player.IdPacket;
import common.packet.player.StepPacket;

import java.awt.Point;
import java.lang.Object;

public class OfflineServer {

    /*
     * This class is used to simulate the server when the game is played offline.
     * It manages the game loop and the game state like collision detection, apple managment.
     */

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());

    public static void init() {
        if(Client.isConnected())
            return;

        PlayerList.clear();
        AppleManager.clear();
        PacketHandler.handle(new SetMapPacket(0));
        AppleManager.spawnAll();
        PacketHandler.handle(new AddPacket(0));
        PacketHandler.handle(new IdPacket(0));
    }

    public static void hanle(Object packet) {
        LOGGER.log(Level.INFO, packet + "\n");
        
        switch (packet) {
            case StepPacket stepPacket -> {
                PacketHandler.handle(stepPacket);
                collectApples(PlayerList.getCurrentPlayer());
            }
            default -> 
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }

    private static void collectApples(NetPlayer player) {
        ArrayList<Point> collectedApples = AppleManager.getCollecteds(player);

        if(collectedApples.isEmpty())
            return;
            
        collectedApples.forEach(apple -> PacketHandler.handle(new EatApplePacket(player.getId(), apple)));
        collectedApples.forEach(apple -> AppleManager.spawn());
    }
}
