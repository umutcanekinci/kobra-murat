package client;

import common.Position;
import common.SnakePart;
import common.Object;
import common.Constants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class SnakePartObject extends SnakePart {

    private Object object;

    public SnakePartObject() {
        super();
        object = new Object(Constants.HIDDEN_PART_POSITION);
    }

    public SnakePartObject(Position point) {
        super(point);
        object = new Object(point);
    }

    @Override
    public void reset() {
        super.reset();
        object.setImage(null);
    }
    

    public void setPosition(Position point) {
        if (point == null)
            throw new IllegalArgumentException("Position cannot be null");

        super.setLocation(point);
        object.setPosition(point);
    }

    public void setImage(BufferedImage image) {
        if (image == null)
            throw new IllegalArgumentException("Image cannot be null");

        object.setImage(image);
    }

    public Position getPosition() {
        return object.getPosition();
    }

    public boolean doesCollide(Position point) {
        if (point == null)
            throw new IllegalArgumentException("Position cannot be null");

        return object.doesCollide(point);
    }

    public void setColliderColor(Color color) {
        object.setColliderColor(color);
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        if (g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        object.draw(g, observer);
    }

}
