package common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import common.graphics.Image;

public class Object extends Image {

    private Position position;

    public Object(Position position) {
        super();
        this.position = position;
    }

    public Object(Position position, File imageFile) {
        super(imageFile);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public BufferedImage getImage() {
        return super.get();
    }

    public void setImage(BufferedImage image) {
        super.set(image);
    }

    public boolean doesCollide(Position position) {
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
        return position.toString();
    }

}
