package client;

import java.awt.event.KeyEvent;

import common.Direction;
import common.Utils;

public class PlayerController {
    private static NetPlayer player;
    private static final int speed = 3; // tiles/second
    private static double displacement = 0;
    private static boolean canRotate = true;

    public static void enableRotation() {
        canRotate = true;
    }

    public static void setPlayer(NetPlayer player) {
        PlayerController.player = player;
    }

    public static void update() {
        if(player == null)
            return;

        move();
    }

    private static void move() {
        displacement += speed * Board.DELTATIME;

        if(displacement < 1)
            return;

        player.step();
        displacement = 0;
        enableRotation();
    }
    
    public static void keyPressed(KeyEvent e) {
        if(player == null)
            return;
        
        int key = e.getKeyCode();
        rotate(key);
    }

    private static void rotate(int key) {
        if(!canRotate || player == null)
            return;

        Direction newDirection = Utils.keyToDirection(key);

        if(newDirection == null)
            return;

        if(newDirection.isParallel(player.getDirection()))
            return;

        player.setDirection(newDirection);
        canRotate = false;
    }
}
