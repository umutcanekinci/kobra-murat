package client;

import java.awt.event.KeyEvent;

public class PlayerController {
    private static Player player;
    private static final int speed = 3; // tiles/second
    private static double displacement = 0;
    
    public static void setPlayer(Player player) {
        PlayerController.player = player;
    }

    public static void keyPressed(KeyEvent e) {
        if(player == null)
            return;
        
        int key = e.getKeyCode();
        player.rotate(key);
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

        Board.onStep();
        displacement = 0;
    }
}
