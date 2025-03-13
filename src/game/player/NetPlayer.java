package game.player;
import java.awt.Point;
import java.util.ArrayList;
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

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Player " + id + (isHost() ? " (Host)" : "") + (PlayerList.isCurrentPlayer(this) ? " (You)" : ""));
        info.add("Length: " + snake.length);
        info.add("Direction: " + snake.getDirection().name());
        for(int i = 0; i < snake.parts.size(); i++) {
            Point part = snake.parts.get(i);
            info.add("Part " + i + ": " + part + (snake.isHead(part) ? " (Head)" : ""));
        }
        return info;
    }

}
