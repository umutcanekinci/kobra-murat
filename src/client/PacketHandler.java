package client;

import java.util.logging.Level;
import java.util.logging.Logger;

import common.packet.EatApplePacket;
import common.packet.RotatePacket;
import common.packet.SpawnApplePacket;
import common.packet.SpawnApplesPacket;
import common.packet.SpawnPacket;
import common.packet.StepPacket;
import common.packet.UpdateTransformPacket;
import common.packet.basic.AddPacket;
import common.packet.basic.IdPacket;
import common.packet.basic.RemovePacket;
import common.packet.basic.ServerClosedPacket;
import common.packet.basic.SetMapPacket;
import common.packet.basic.StartPacket;

public class PacketHandler {

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());

    private PacketHandler() {}

    public static void handle(Object packet) {
        LOGGER.log(Level.INFO, "{0}\n", packet);

        if (packet == null) {
            LOGGER.log(Level.WARNING, "Received null packet\n");
            return;
        }

        switch (packet) {
            case IdPacket idPacket ->
                PlayerList.setId(idPacket.getId());
    
            case SetMapPacket setMapPacket ->
                Tilemap.load(setMapPacket);
                
            case AddPacket addPacket ->
                PlayerList.add(addPacket.getId());
                
            case SpawnPacket spawnPacket -> {
                PlayerList.spawn(spawnPacket.getId(), spawnPacket.getSpawnPoint(), spawnPacket.getLength());
                
                if(spawnPacket.getId() == PlayerList.getId())
                    Game.start();
            }

            case UpdateTransformPacket transformPacket ->
                PlayerList.updateTransform(transformPacket.getId(), transformPacket.getDirection(), transformPacket.getParts(), transformPacket.getTailIndex());

            case StartPacket startPacket ->
                Game.start();

            case EatApplePacket eatApplePacket -> {
                AppleManager.remove(eatApplePacket.getPosition());
                PlayerList.grow(eatApplePacket.getId(), 1);
            }

            case SpawnApplesPacket spawnApplesPacket ->
                AppleManager.addAll(spawnApplesPacket.getPositions());

            case SpawnApplePacket spawnApplePacket ->
                AppleManager.add(spawnApplePacket.getPosition());
                
            case RotatePacket rotatePacket ->
                PlayerList.rotate(rotatePacket.getId(), rotatePacket.getDirection());

            case StepPacket stepPacket ->
                PlayerList.step(stepPacket.getId(), stepPacket.getDirection());
                
            case RemovePacket removePacket ->
                PlayerList.remove(removePacket.getId());
                
            case ServerClosedPacket serverClosedPacket ->
                Client.close();

            default ->
                LOGGER.log(Level.WARNING, "Unknown packet: {0}\n", packet);
        }
    }

}
