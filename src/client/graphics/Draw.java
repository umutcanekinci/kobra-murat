package client.graphics;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

import common.Constants;

import client.AppleManager;
import client.DebugLog;
import client.Tilemap;
import client.PlayerList;

public class Draw {

    private static final Color MENU_BACKGROUND_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.BLACK;

    public static void all(Graphics2D g, JPanel panel, boolean isGameStarted, boolean debugMode, ImageObserver observer) {
        /*
         when calling g.drawImage() we can use "this" for the ImageObserver
         because Component implements the ImageObserver interface, and JPanel
         extends from Component. So "this" Board instance, as a Component, can
         react to imageUpdate() events triggered by g.drawImage()
        */

        UI.initGraphics(g);
        background(panel, isGameStarted);
        map(g, isGameStarted, observer);
        apples(g, isGameStarted, observer);
        players(g, isGameStarted, observer);
        score(g, isGameStarted);
        mainMenu(g, isGameStarted);
        colliders(g, isGameStarted, debugMode);
        playerBoard(g, isGameStarted);
        debug(g, debugMode);

        Toolkit.getDefaultToolkit().sync();  // this smooths out animations on some systems
    }

    private static void background(JPanel panel, Boolean isGameStarted) {
        panel.setBackground(isGameStarted ? BACKGROUND_COLOR : MENU_BACKGROUND_COLOR);
    }

    private static void map(Graphics2D g, boolean isGameStarted, ImageObserver observer) {
        if(!isGameStarted || !Tilemap.isReady() || g == null)
            return;
        
        Tilemap.draw(g, observer);
    }

    private static void apples(Graphics2D g, boolean isGameStarted, ImageObserver observer) {
        if(g == null || !isGameStarted)
            return;

        AppleManager.draw(g, observer);
    }
    
    private static void players(Graphics2D g, boolean isGameStarted, ImageObserver observer) {
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

        UI.drawTitle(g, Constants.SIZE.width, 100, null);
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

        UI.drawPlayerBoard(g, Constants.SIZE.width - 200, 0, 200);
    }

    private static  void debug(Graphics2D g, boolean debugMode) {
        if(!debugMode)
            return;

        DebugLog.draw(g);
    }

}
