package server;

import java.util.List;

import common.Constants;
import common.Position;
import common.Connection;
import common.packet.EatApplePacket;
import common.packet.SpawnApplePacket;
import common.packet.SpawnPacket;
import common.packet.StepPacket;

public class NetPlayer extends Player{

    private static double displacement = 0;
    private Connection connection;
    private final int id;

    public NetPlayer(Connection connection, int id) {
        super();
        this.connection = connection;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void send(Object packet) {
        connection.sendData(packet);
    }

    public void move() {
        displacement += getSpeed() * Constants.DELTATIME;
        if(displacement < 1)
            return;

        displacement = 0;
        //enableRotation();

        Position position = getNextPosition();
        
        boolean doesHitSelf = doesCollide(position) && !isPointOnTail(position);
        if(doesHitSelf || Tilemap.doesCollide(position)) {
            SpawnPacket packet = new SpawnPacket(id, Tilemap.getSpawnPoint(), Constants.DEFAULT_LENGTH);
            PlayerList.spawnPlayer(packet);
            PlayerList.sendToAll(packet);
            return;
        }

        StepPacket packet = new StepPacket(id, getDirection());
        PlayerList.playerStep(packet);
        PlayerList.sendToAll(packet);
        collectApples();
    }

    private void collectApples() {
        List<Position> collectedApples = AppleManager.getCollecteds(this);

        if(collectedApples.isEmpty())
            return;

        AppleManager.removeAll(collectedApples);
        collectedApples.forEach(apple -> PlayerList.sendToAll(new EatApplePacket(id, apple)));
        collectedApples.forEach(apple -> PlayerList.sendToAll(new SpawnApplePacket(AppleManager.spawn())));
        grow(collectedApples.size());
    }

    public void close() {
        if(connection == null)
            return;

        connection.close();
    }

    @Override
    public String toString() {
        return "Player " + id +
        (isHost()          ? " (Host)" : "") +
        "\n" + super.toString();
    }

    public boolean isHost() {
        return id == 0;
    }


}
