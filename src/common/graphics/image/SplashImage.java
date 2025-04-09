package common.graphics.image;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.swing.Timer;

public class SplashImage extends BackgroundImage implements MouseListener {

    //region ---------------------------------------- Variables ------------------------------------------

    private static SplashImage INSTANCE;
    private static final String IMAGE_PATH = "images/splash.png";
    private static boolean isFadingIn;
    private static int alpha;
    private static double scale = 1;
    private static final int SPLASH_TIME = 40;
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

    private static ArrayList<SplashListener> listeners = new ArrayList<>();

    //endregion

    private SplashImage() {
        super(IMAGE_PATH);
    }

    public static SplashImage getInstance() {
        if(INSTANCE == null)
            INSTANCE = new SplashImage();

        return INSTANCE;
    }

    public static void addListener(SplashListener splashListener) {
        if (splashListener == null)
            throw new IllegalArgumentException("SplashListener cannot be null.");

        listeners.add(splashListener);
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
        listeners.forEach(SplashListener::onSplashFinished);
    }

    @Override
    public void draw(Graphics g, ImageObserver observer) {
        Graphics2D g2d = (Graphics2D) g;

        if (scale > 1) {
            g2d.scale(scale, scale);
            g2d.translate(-getWidth() / 2 * (scale - 1), -getHeight() / 2 * (scale - 1));
        }

        if (alpha > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
            super.draw(g, observer);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Reset alpha
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
