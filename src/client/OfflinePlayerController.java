package client;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import common.Position;
import common.Constants;
import common.Direction;
import common.Utils;
import common.packet.AddPacket;
import common.packet.EatApplePacket;
import common.packet.StepPacket;
import common.packet.basic.IdPacket;
import common.packet.basic.SetMapPacket;

public class OfflinePlayerController {    
    /*
     * This class is used to simulate the server when the game is played offline.
     * It manages the game loop and the game state like collision detection, apple managment.
     */

    private static NetPlayer player;
    private static double displacement = 0;
    private static boolean canRotate = true;

    public static void init() {
        PlayerList.clear();
        AppleManager.clear();
        PacketHandler.handle(new SetMapPacket(1));
        AppleManager.spawnAll();
        PacketHandler.handle(new AddPacket(0, Constants.DEFAULT_LENGTH));
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
        displacement += player.getSpeed() * Constants.DELTATIME;

        if(displacement < 1)
            return;

        displacement = 0;
        enableRotation();
        
        Position position = player.getNextPosition();
        if(doesCollide(position)) {
            player.spawn(Tilemap.getSpawnPoint());
            return;
        }
        PacketHandler.handle(new StepPacket(player));
        collectApples(position);
    }

    private static boolean doesCollide(Position position) {
        Boolean doesHitSelf = player.doesCollide(position) && !player.isPointOnTail(position);
        if(doesHitSelf || Tilemap.doesCollide(position))
            return true;
        return false;
    }

    private static void collectApples(Position position) {
        ArrayList<Position> collectedApples = AppleManager.getCollecteds(position);

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
