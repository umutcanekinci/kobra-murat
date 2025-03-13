package game.graphics;
import java.util.ArrayList;
import game.Apple;
import network.PlayerList;
import java.awt.Graphics2D;
import game.map.Tilemap;

public class InGame {

    public static void drawMap(Graphics2D g) {
        if(!Tilemap.isReady())
            return;
        
        Tilemap.render(g);
    }

    public static void drawApples(ArrayList<Apple> apples, Graphics2D g) {
        apples.forEach(a -> a.draw(g, null));
    }
    
    public static void drawPlayers(Graphics2D g) {
        PlayerList.players.values().forEach(p -> p.snake.draw(g, null));
    }

}
