package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class GameManager implements ActionListener {

    /* This class will manage the movements of players with a main update loop. */

    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    public void start() {
        initTimer();
    }

    private void initTimer() {
        new Timer(DELTATIME_MS, this).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }
    
    private void update() {
        if(!Server.isRunning())
            return;

        PlayerList.players.values().forEach(player -> player.move());
    }
}
