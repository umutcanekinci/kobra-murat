package client;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import common.Direction;
import common.Utils;
import common.packet.SetMapPacket;
import common.packet.apple.EatApplePacket;
import common.packet.player.AddPacket;
import common.packet.player.IdPacket;

public class OfflinePlayerController {    
    /*
     * This class is used to simulate the server when the game is played offline.
     * It manages the game loop and the game state like collision detection, apple managment.
     */

    private static NetPlayer player;
    private static final int speed = 3; // tiles/second
    private static double displacement = 0;
    private static boolean canRotate = true;

    public static void init() {
        if(Client.isConnected())
            return;

        PlayerList.clear();
        AppleManager.clear();
        PacketHandler.handle(new SetMapPacket(0));
        AppleManager.spawnAll();
        PacketHandler.handle(new AddPacket(0));
        PacketHandler.handle(new IdPacket(0));
    }

    public static void enableRotation() {
        canRotate = true;
    }
    
    public static void setPlayer(NetPlayer player) {
        OfflinePlayerController.player = player;
    }

    public static void update() {
        if(Client.isConnected() ||player == null)
            return;

        move();
        collectApples(PlayerList.getCurrentPlayer());
    }

    private static void move() {
        displacement += speed * Board.DELTATIME;

        if(displacement < 1)
            return;

        player.step();
        displacement = 0;
        enableRotation();
    }

    private static void collectApples(NetPlayer player) {
        ArrayList<Point> collectedApples = AppleManager.getCollecteds(player);

        if(collectedApples.isEmpty())
            return;
            
        collectedApples.forEach(apple -> PacketHandler.handle(new EatApplePacket(player.getId(), apple)));
        collectedApples.forEach(apple -> AppleManager.spawn());
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
