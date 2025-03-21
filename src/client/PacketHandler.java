package client;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Object;

import common.packet.AddPacket;
import common.packet.EatApplePacket;
import common.packet.RotatePacket;
import common.packet.SpawnApplePacket;
import common.packet.StepPacket;
import common.packet.UpdateTransformPacket;
import common.packet.basic.IdPacket;
import common.packet.basic.RemovePacket;
import common.packet.basic.ServerClosedPacket;
import common.packet.basic.SetMapPacket;

public class PacketHandler {

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());

    public static void handle(Object packet) {
        LOGGER.log(Level.INFO, packet + "\n");

        switch (packet) {
            case IdPacket idPacket -> {
                PlayerList.setId(idPacket);
            }
    
            case SetMapPacket setMapPacket -> {
                Tilemap.load(setMapPacket);
                AppleManager.setEmptyTiles(Tilemap.getEmptyTiles());
            }
                
            case AddPacket addPacket ->
                PlayerList.addPlayer(addPacket);
            
                
            case UpdateTransformPacket playerTransformPacket ->
                PlayerList.updatePlayerTransform(playerTransformPacket);


            case EatApplePacket eatApplePacket -> {
                AppleManager.remove(eatApplePacket.getPosition());
                PlayerList.grow(eatApplePacket.getId(), 1);
            }

            case SpawnApplePacket spawnApplePacket ->
                AppleManager.add(spawnApplePacket.getPosition());
                
            case RotatePacket rotatePacket ->
                PlayerList.rotatePlayer(rotatePacket);

            case StepPacket stepPacket ->
                PlayerList.playerStep(stepPacket);
                
            case RemovePacket removePacket ->
                PlayerList.removePlayer(removePacket);
                
            case ServerClosedPacket serverClosedPacket -> {
                Client.close();
                Game.openMenu();
            }

            default ->
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
        }
    }

}
