package game.player;
import java.awt.Point;
import java.util.ArrayList;

import game.Board;
import network.Connection;
import network.PlayerList;
public class NetPlayer extends Player{

    public Connection connection;
    public int id;

    public NetPlayer(Board board, int id) {
        super(board, new Snake());
        this.id = id;
    }

    public NetPlayer(Board board, int id, Snake snake, Connection connection) {
        this(board, id);
        this.connection = connection;
    }

    public boolean isHost() {
        return id == 0;
    }

    public String[] getDebugInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Player " + id + (isHost() ? " (Host)" : "") + (PlayerList.isCurrentPlayer(this) ? " (You)" : ""));
        info.add("Length: " + snake.length);
        info.add("Direction: " + snake.direction.name());
        for(int i = 0; i < snake.parts.size(); i++) {
            Point part = snake.parts.get(i);
            info.add("Part " + i + ": (" + part.x + ", " + part.y + ")");
        }
        return info.toArray(new String[0]);
    }

}
