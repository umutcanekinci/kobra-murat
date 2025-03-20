package client;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import common.Position;

public class Camera {

    private static int x, y;
    private static int width, height;
    private static int mapWidth, mapHeight;

    public static String getInfo() {
        return "Camera: " + x + ", " + y + " - " + width + ", " + height + " - " + mapWidth + ", " + mapHeight;
    }

    public static void init(int width, int height, int mapWidth, int mapHeight) {
        Camera.width = width;
        Camera.height = height;
        Camera.mapWidth = mapWidth;
        Camera.mapHeight = mapHeight;
    }

    public static void focus(Position position) {
        position = position.getScreenPosition();
        x = position.x - width / 2;
        y = position.y - height / 2;

        if(x < 0)
            x = 0;
        if(y < 0)
            y = 0;
        if(x > mapWidth - width)
            x = mapWidth - width;
        if(y > mapHeight - height)
            y = mapHeight - height;
    }

    public static Graphics2D draw(Graphics2D renderer, ImageObserver observer) {
        if(renderer == null)
            return renderer;

        renderer.translate(-x, -y);

        return renderer;
    }

}
