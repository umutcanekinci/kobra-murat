package common.graphics.image;

import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class BackgroundImage extends Image {

    public static final BackgroundImage MAIN_MENU = new BackgroundImage("images/main-menu-background.png");
    public static final BackgroundImage LOBBY     = new BackgroundImage("images/lobby.png");

    public BackgroundImage(String imagePath) {
        super(imagePath);
    }
    
    public void draw(Graphics g, ImageObserver observer) {
        super.draw((Graphics2D) g, 0, 0, observer);
    }
}
