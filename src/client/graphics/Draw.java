package client.graphics;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

import client.AppleManager;
import client.DebugLog;
import client.Tilemap;
import client.PlayerList;
import common.Level;

public class Draw {

    public static void all(Graphics g, boolean isGameStarted, boolean debugMode, ImageObserver observer) {
        /*
         when calling g.drawImage() we can use "this" for the ImageObserver
         because Component implements the ImageObserver interface, and JPanel
         extends from Component. So "this" Board instance, as a Component, can
         react to imageUpdate() events triggered by g.drawImage()
        */

        Graphics2D g2d = (Graphics2D) g; // we need to cast the Graphics to Graphics2D to draw nicer text
        UI.initGraphics(g2d);

        map(g2d, isGameStarted, observer);
        apples(g2d, isGameStarted, observer);
        players(g2d, isGameStarted, observer);
        score(g2d, isGameStarted);
        mainMenu(g2d, isGameStarted);
        colliders(g2d, isGameStarted, debugMode);
        playerBoard(g2d, isGameStarted);
        debug(g2d, debugMode);

        Toolkit.getDefaultToolkit().sync();  // this smooths out animations on some systems
    }

    public static void map(Graphics2D g, boolean isGameStarted, ImageObserver observer) {
        if(!isGameStarted || !Tilemap.isReady() || g == null)
            return;
        
        Tilemap.draw(g, observer);
    }

    public static void apples(Graphics2D g, boolean isGameStarted, ImageObserver observer) {
        if(g == null || !isGameStarted)
            return;

        AppleManager.draw(g, observer);
    }
    
    public static void players(Graphics2D g, boolean isGameStarted, ImageObserver observer) {
        if(g == null || !isGameStarted)
            return;

        PlayerList.draw(g, observer);
    }

    private static void score(Graphics2D g, boolean isGameStarted) {
        if(!isGameStarted)
            return;

        //UI.drawScore(g, player == null ? 0 : player.getScore());
    }

    private static void mainMenu(Graphics2D g, boolean isGameStarted) {
        if(isGameStarted)
            return;

        UI.drawTitle(g, Level.SIZE.width, 100, null);
    }

    private static  void colliders(Graphics2D g, boolean isGameStarted, boolean debugMode) {
        if(!debugMode || !isGameStarted)
            return;

        PlayerList.drawColliders(g);
        Tilemap.drawColliders(g);
        AppleManager.drawColliders(g);
    }

    private static void playerBoard(Graphics2D g, boolean isGameStarted) {
        if(!isGameStarted)
            return;

        UI.drawPlayerBoard(g, Level.SIZE.width - 200, 0, 200);
    }

    private static  void debug(Graphics2D g, boolean debugMode) {
        if(!debugMode)
            return;

        DebugLog.draw(g);
    }

}
