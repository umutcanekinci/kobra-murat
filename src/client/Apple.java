package client;

import java.io.File;

import common.Position;
import common.Object;

public class Apple extends Object {

    private static final File IMAGE_FILE = new File("images/apple.png");

    public Apple(Position position) {
        super(position, IMAGE_FILE);
    }

}