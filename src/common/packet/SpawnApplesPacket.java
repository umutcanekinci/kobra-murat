package common.packet;

import java.util.ArrayList;

import common.Position;

public class SpawnApplesPacket extends Packet {
    private ArrayList<Position> positions;

    public SpawnApplesPacket(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }
}
