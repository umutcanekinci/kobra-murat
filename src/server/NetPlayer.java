package server;

import common.Connection;

public class NetPlayer extends Player{

    public Connection connection;
    public final int id;

    public NetPlayer(int id) {
        super(new Snake());
        this.id = id;
    }

    public NetPlayer(int id, Snake snake, Connection connection) {
        this(id);
        this.connection = connection;
    }

    public String toString() {
        String info = 
        "Player " + id + "\n" +
        snake.toString();
        return info;
    }

}
