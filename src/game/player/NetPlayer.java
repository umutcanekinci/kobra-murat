package game.player;
import java.awt.Point;
import network.Connection;
import network.PlayerList;

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

    public boolean isHost() {
        return id == 0;
    }

    public boolean isCurrentPlayer() {
        return PlayerList.id == id;
    }

    public String toString() {
        String info = 
        "Player " + id +
        (isHost()          ? " (Host)" : "") +
        (isCurrentPlayer() ? " (You)"  : "") + "\n" +
        "Length: " + snake.length + "\n" +
        "Direction: " + snake.getDirection().name() + "\n";
        
        for(int i = 0; i < snake.parts.size(); i++) {
            Point part = snake.parts.get(i);
            info += "Part " + i + ": " + part + (snake.isHead(part) ? " (Head)" : "") + "\n";
        }
        
        return info;
    }

}
