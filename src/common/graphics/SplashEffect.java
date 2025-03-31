package common.graphics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import javax.swing.Timer;

import client.GameListener;

public class SplashEffect implements GameListener, MouseListener {
    
    private static boolean isFadingIn;
    private static int alpha;
    private static double scale = 1;
    private static int SPLASH_TIME = 40;
    private static final Timer TIMER = new Timer(SPLASH_TIME, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            scale += 0.0015;

            if(isFadingIn)
                alpha += 5;
            else {
                alpha -= 10;
            
                if(alpha <= 0)
                    stop();
            }
            
            if (alpha >= 255)
                isFadingIn = false;
        }
    });

    private static SplashListener listener;

    public static void setListener(SplashListener splashListener) {
        listener = splashListener;
    }

    @Override
    public void onWindowReady() {
        start();
    }

    public static void start() {
        alpha = 0;
        isFadingIn = true;
        TIMER.start();
    }

    public static boolean isPlaying() {
        return TIMER.isRunning();
    }

    public static void stop() {
        alpha = 0;
        isFadingIn = true;
        TIMER.stop();
        listener.onSplashFinished();
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        if (scale > 1) {
            g.scale(scale, scale);
            g.translate(-Image.SPLASH.getWidth() / 2 * (scale - 1), -Image.SPLASH.getHeight() / 2 * (scale - 1));
        }

        if (alpha > 0) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
            g.drawImage(Image.SPLASH.get(), 0,0 , observer);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Reset alpha
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isPlaying())
            stop();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public static void keyPressed(KeyEvent e) {
        if (isPlaying())
            stop();
    }
}
