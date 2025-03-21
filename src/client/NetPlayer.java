package client;

import common.Position;
import common.packet.AddPacket;

public class NetPlayer extends Player{

    private final int id;

    public NetPlayer(AddPacket packet) {
        this(packet.getId(), packet.getLength(), packet.getSpawnPoint());
    }

    public NetPlayer(int id, int defaultLength, Position spawnPoint) {
        super(defaultLength, spawnPoint);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        String info = 
        "Player " + id +
        (isHost()          ? " (Host)" : "") +
        (isCurrentPlayer() ? " (You)"  : "")
        + "\n" + super.toString();
        
        return info;
    }

    public boolean isHost() {
        return id == 0;
    }

    public boolean isCurrentPlayer() {
        return PlayerList.getId() == id;
    }

}
