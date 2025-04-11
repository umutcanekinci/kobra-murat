package client;

import java.util.List;

import common.Position;
import common.Constants;
import common.Direction;
import common.packet.EatApplePacket;
import common.packet.SpawnPacket;
import common.packet.StepPacket;
import common.packet.basic.AddPacket;
import common.packet.basic.IdPacket;
import common.packet.basic.SetMapPacket;

public class OfflinePlayerController {    
    /*
     * This class is used to simulate the server when the game is played offline.
     * It manages the game loop and the game state like collision detection, apple managment.
     */

    private static OfflinePlayerController instance;
    private static NetPlayer player;
    private static double displacement = 0;
    private static boolean canRotate = true;

    private OfflinePlayerController() {}

    public static OfflinePlayerController getInstance() {
        if(instance == null)
            instance = new OfflinePlayerController();
        return instance;
    }

    public static void init() {
        PlayerList.clear();
        PacketHandler.handle(new SetMapPacket(1));
        AppleManager.spawnAll();
        PacketHandler.handle(new AddPacket(0));
        PacketHandler.handle(new IdPacket(0));
        PacketHandler.handle(new SpawnPacket(0, Tilemap.getSpawnPoint(), Constants.DEFAULT_LENGTH));
    }

    public static void enableRotation() {
        canRotate = true;
    }
    
    public static void setPlayer(NetPlayer player) {
        if(player == null)
            throw new NullPointerException("Player cannot be null");

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
            player.spawn(Tilemap.getSpawnPoint(), Constants.DEFAULT_LENGTH);
            return;
        }
        PacketHandler.handle(new StepPacket(player));
        collectApples(position);
    }

    private static boolean doesCollide(Position position) {
        if(position == null)
            throw new NullPointerException("Position cannot be null");

        Boolean doesHitSelf = player.doesCollide(position) && !player.isPointOnTail(position);
        return doesHitSelf || Tilemap.doesCollide(position);
    }

    private static void collectApples(Position position) {
        if(position == null)
            throw new NullPointerException("Position cannot be null");

        List<Position> collectedApples = AppleManager.getCollides(position);

        if(collectedApples.isEmpty())
            return;
            
        collectedApples.forEach(apple -> PacketHandler.handle(new EatApplePacket(player.getId(), apple)));
        collectedApples.forEach(apple -> AppleManager.spawnAtRandom());
    }

    static void rotate(Direction newDirection) {
        if(!canRotate || player == null)
            return;

        

        if(newDirection.isParallel(player.getDirection()))
            return;

        player.setDirection(newDirection);
        canRotate = false;
    }
}
