package game;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import game.map.Tilemap;
import game.player.NetPlayer;
import network.client.PlayerList;
import network.packet.apple.SpawnApplePacket;

public class AppleManager {

    public static final int APPLE_COUNT = 5;
    private static ArrayList<Apple> apples = new ArrayList<>();
    
    public static void addApple(Apple apple) {
        apples.add(apple);
    }

    public static void addApple(Point position) {
        apples.add(new Apple(position));
    }

    public static void addApple(SpawnApplePacket packet) {
        apples.add(new Apple(packet.position));
    }

    public static void spawnApples() {
        for (int i = 0; i < APPLE_COUNT - apples.size(); i++) {
            spawnApple();
        }
    }

    public static void spawnApple() {
        apples.add(new Apple(getRandomApplePosition()));
    }

    public static Point getRandomApplePosition() {
        Point position; boolean isPointOnSnake;

        while(true) {
            position = Tilemap.getRandomEmptyPoint();
            for(Apple apple : apples) {
                if(apple.doesCollide(position)) {
                    continue;
                }
            }

            isPointOnSnake = false;
            for (NetPlayer player : PlayerList.players.values()) {
                if (player.snake.doesCollide(position)) {
                    isPointOnSnake = true;
                    break;
                }
            }

            if(!isPointOnSnake)
                break;

        }

        return position;

    }

    public static Point getRandomPoint() {
        return Utils.getRandomPoint(Tilemap.getCols(), Tilemap.getRows());
    }

    public static void removeAll(ArrayList<Apple> applesToRemove) {
        apples.removeAll(applesToRemove);
    }

    static ArrayList<Apple> getCollecteds() {
        ArrayList<Apple> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            for(NetPlayer player : PlayerList.players.values()) {
                if (apple.doesCollide(player.getPos())) {
                    player.snake.grow(1);
                    collectedApples.add(apple);
                    break;
                }
            }
        }
        return collectedApples;
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        apples.forEach(apple -> apple.draw(g, observer));
    }

    public static void drawColliders(Graphics2D g) {
        apples.forEach(apple -> apple.drawCollider(g));
    }

    public static void clear() {
        apples.clear();
    }

    public static String getInfo() {
        String str = "APPLES (" + apples.size() + ")\n";

        if(apples.isEmpty()) {
            str += "No apples.\n";
        }

        for (Apple apple : apples) {
            str += apple + "\n";
        }

        return str;
    }

}
