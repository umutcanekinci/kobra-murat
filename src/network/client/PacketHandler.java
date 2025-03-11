package network.client;
import network.Connection;
import network.PlayerList;
import network.packet.ServerClosedPacket;
import network.packet.SetMapPacket;
import network.packet.apple.SpawnApplePacket;
import network.packet.player.AddPacket;
import network.packet.player.IdPacket;
import network.packet.player.UpdateTransformPacket;
import network.packet.player.RemovePacket;
import network.packet.player.StepPacket;
import game.Board;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketHandler {

    private static Board board;
    private static Client client;
    private static final Logger LOGGER = Logger.getLogger(PacketHandler.class.getName());

    public static void init(Board board, Client client) {
        PacketHandler.client = client;
        PacketHandler.board = board;
    }

    public static void handle(Object packet, Connection connection) {
        LOGGER.log(Level.INFO, "Client received a packet: " + packet + "\n");

        switch (packet) {
            case IdPacket idPacket -> {
                PlayerList.id = idPacket.id;
                board.initPlayer();
            }

            case SetMapPacket setMapPacket ->
                board.setMap(setMapPacket.id);

            case AddPacket addPacket -> {
                if (PlayerList.players.containsKey(addPacket.id))
                    return;

                PlayerList.addPlayer(board, connection, addPacket);
            }
            case SpawnApplePacket spawnApplePacket ->
                board.spawnApple(spawnApplePacket.position);
            case UpdateTransformPacket playerTransformPacket ->
                PlayerList.updatePlayerTransform(playerTransformPacket);

            case StepPacket stepPacket ->
                PlayerList.playerStep(stepPacket);

            case RemovePacket removePacket ->
                PlayerList.removePlayer(removePacket);
                
            case ServerClosedPacket serverClosedPacket -> {
                client.close();
                board.openMenu();
            }

            default -> {
                LOGGER.log(Level.WARNING, "Unknown packet: " + packet + "\n");
            }
        }
    }

}
