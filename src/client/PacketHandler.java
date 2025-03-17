package client;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Object;

import common.packet.ServerClosedPacket;
import common.packet.SetMapPacket;
import common.packet.apple.EatApplePacket;
import common.packet.apple.SpawnApplePacket;
import common.packet.player.AddPacket;
import common.packet.player.IdPacket;
import common.packet.player.RemovePacket;
import common.packet.player.RotatePacket;
import common.packet.player.StepPacket;
import common.packet.player.UpdateTransformPacket;

public class PacketHandler {

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());

    public static void handle(Object packet) {
        LOGGER.log(Level.INFO, packet + "\n");

        switch (packet) {
            case IdPacket idPacket -> {
                PlayerList.setId(idPacket.id);
            }

            case SetMapPacket setMapPacket ->
                Board.setMap(setMapPacket.id);

            case AddPacket addPacket ->
                PlayerList.addPlayer(addPacket);
            
            case EatApplePacket eatApplePacket -> {
                AppleManager.remove(eatApplePacket.position);
                PlayerList.getPlayer(eatApplePacket.id).grow(1);
            }

            case SpawnApplePacket spawnApplePacket ->
                AppleManager.add(spawnApplePacket.position);
                
            case RotatePacket rotatePacket ->
                PlayerList.rotatePlayer(rotatePacket);

            case UpdateTransformPacket playerTransformPacket ->
                PlayerList.updatePlayerTransform(playerTransformPacket);

            case StepPacket stepPacket ->
                PlayerList.playerStep(stepPacket);

            case RemovePacket removePacket ->
                PlayerList.removePlayer(removePacket);
                
            case ServerClosedPacket serverClosedPacket -> {
                Client.close();
                Board.openMenu();
            }

            default ->
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }

}
