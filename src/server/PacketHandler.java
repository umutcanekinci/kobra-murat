package server;

import java.util.logging.Level;
import java.util.logging.Logger;

import common.packet.RotatePacket;
import common.packet.SpawnPacket;
import common.packet.basic.DisconnectPacket;
import common.packet.basic.RemovePacket;
import common.packet.basic.StartPacket;
import common.Connection;
import common.Constants;

public class PacketHandler {
    
    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());
    public static void handle(Object packet, Connection connection) {
        LOGGER.log(Level.INFO, packet + "\n");
        
        switch (packet) {
            case StartPacket startPacket -> {
                SpawnPacket spawnPacket = new SpawnPacket(startPacket.getId(), Tilemap.getSpawnPoint(), Constants.DEFAULT_LENGTH);
                PlayerList.spawnPlayer(spawnPacket);
                PlayerList.sendToAll(spawnPacket);
            }

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
