package server;

import client.Board;
import common.Connection;
import common.packet.player.StepPacket;

public class NetPlayer extends Player{

    private static final int speed = 3; // tiles/second
    private static double displacement = 0;
    private Connection connection;
    private final int id;

    public String toString() {
        String info = 
        "Player " + id + "\n" +
        super.toString();
        return info;
    }

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

    public void close() {
        if(connection == null)
            return;

        connection.close();
    }

    
    public void move() {
        displacement += speed * Board.DELTATIME;

        if(displacement < 1)
            return;

        send(new StepPacket(this));
    }
}
