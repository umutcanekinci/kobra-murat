package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import common.Constants;
import common.ServerListener;

public class GameManager implements ActionListener, ServerListener {

    /* This class will manage the movements of players with a main update loop. */
    private static Timer timer;

    @Override
    public void onServerConnected(String ip) {
        start();
    }

    public void start() {
        timer = new Timer(Constants.DELTATIME_MS, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }
    
    private void update() {
        PlayerList.players.values().forEach(player -> player.move());
    }

    @Override
    public void onServerClosed() {
        timer.stop();
    }

}
