package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import common.Constants;

public class GameManager implements ActionListener {

    /* This class will manage the movements of players with a main update loop. */

    public void start() {
        initTimer();
    }

    private void initTimer() {
        new Timer(Constants.DELTATIME_MS, this).start();
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
