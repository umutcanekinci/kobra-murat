package client;

import java.awt.Point;
import java.io.File;

public class Apple extends Object {

    private static final File IMAGE_FILE = new File("images/apple.png");

    public Apple(Point position) {
        super(position, IMAGE_FILE);
    }

}