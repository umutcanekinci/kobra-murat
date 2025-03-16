package client;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.Connection;
import common.packet.ServerClosedPacket;
import common.packet.SetMapPacket;
import common.packet.apple.SpawnApplePacket;
import common.packet.player.AddPacket;
import common.packet.player.IdPacket;
import common.packet.player.RemovePacket;
import common.packet.player.StepPacket;
import common.packet.player.UpdateTransformPacket;
import java.lang.Object;

public class PacketHandler {

    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());

    public static void handle(Object packet, Connection connection) {
        LOGGER.log(Level.INFO, packet + "\n");

        switch (packet) {
            case IdPacket idPacket -> {
                PlayerList.setId(idPacket.id);
                Board.initPlayer();
            }

            case SetMapPacket setMapPacket ->
                Board.setMap(setMapPacket.id);

            case AddPacket addPacket -> {
                PlayerList.addPlayer(connection, addPacket);
            }
            
            case SpawnApplePacket spawnApplePacket ->
                AppleManager.addApple(spawnApplePacket);
                
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

            default -> {
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
            }
        }
    }

}
