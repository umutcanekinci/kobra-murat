package game.graphics;
import java.util.ArrayList;
import game.Apple;
import network.PlayerList;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import game.map.Tilemap;

public class InGame {

    public static void drawMap(Graphics2D g, ImageObserver observer) {
        if(!Tilemap.isReady())
            return;
        
        Tilemap.draw(g, observer);
    }

    public static void drawApples(Graphics2D g, ArrayList<Apple> apples, ImageObserver observer) {
        apples.forEach(a -> a.draw(g, observer));
    }
    
    public static void drawPlayers(Graphics2D g, ImageObserver observer) {
        PlayerList.players.values().forEach(p -> p.snake.draw(g, observer));
    }

}
