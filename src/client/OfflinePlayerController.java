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
import common.packet.player.StepPacket;

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
        move();
    }

    private static void move() {
        displacement += speed * Board.DELTATIME;

        if(displacement < 1)
            return;

        displacement = 0;
        enableRotation();
        
        Point position = player.getNextPosition();
        if(doesCollide(position)) {
            player.reset();
            return;
        }
        collectApples(position);
        PacketHandler.handle(new StepPacket(player.getId(), player.getDirection()));
    }

    private static boolean doesCollide(Point position) {
        Boolean doesHitSelf = player.doesCollide(position) && !player.isPointOnTail(position);
        if(doesHitSelf || Tilemap.doesCollide(position))
            return true;
        return false;
    }

    private static void collectApples(Point position) {
        ArrayList<Point> collectedApples = AppleManager.getCollecteds(position);

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
