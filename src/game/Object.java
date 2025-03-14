package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

public class Object extends Image {

    private Point position;

    public Object() {
        super();
    }

    public Object(Point position) {
        super();
        this.position = position;
    }

    public Object(Point position, File imageFile) {
        super(imageFile);
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public BufferedImage getImage() {
        return super.get();
    }

    public void setImage(BufferedImage image) {
        super.set(image);
    }

    public boolean doesCollide(Point position) {
        if(this.position == null)
            return false;

        return this.position.equals(position);
    }


    public void draw(Graphics2D g, ImageObserver observer) {
        super.draw(g, position, observer);
    }

    public void drawCollider(Graphics2D g) {
        super.drawBorder(g, position);
    }

    public void drawCollider(Graphics2D g, Color color) {
        super.drawBorder(g, color, position);
    }

    @Override
    public String toString() {
        return "[" + position.x + ", " + position.y + "]";
    }

}
