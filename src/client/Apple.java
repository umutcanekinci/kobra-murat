package client;

import common.Position;
import common.graphics.image.Image;
import common.Object;

/**
 * The Apple class represents an apple object in the game.
 * It extends the Object class and is used to create apple objects at specific positions.
 * @version 1.0
 * @see Object
 */
public class Apple extends Object {

    /**
     * Constructor for the Apple class.
     * @param position The position of the apple in the game world.
     * @version 1.0
     * @see Object#Object(Position)
     */
    public Apple(Position position) {
        super(position, Image.APPLE);
    }

}