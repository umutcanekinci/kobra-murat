package common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JComponent;

import client.DebugLog;

import java.awt.Dimension;
import java.awt.Graphics;

import common.graphics.image.Image;

public class Object extends JComponent {

    private Color colliderColor = Color.BLACK;
    protected Position position;
    protected Image image = null;

    public Object() {
        super();
        this.position = new Position();
    }

    public Object(Position position) {
        super();
        this.position = position;
    }

    public Object(Position position, String imagePath) {
        this(position);
        this.image = new Image(imagePath);
    }

    public Object(Position position, Image image) {
        this(position);
        setImage(image.get());
    }

    public void setColliderColor(Color color) {
        if(color == null)
            throw new IllegalArgumentException("Color cannot be null");

        this.colliderColor = color;
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }
    
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public BufferedImage getImage() {
        return image.get();
    }

    public void setImage(BufferedImage image) {
        if(this.image == null)
            this.image = new Image();

        this.image.set(image);
    }

    public boolean doesCollide(Position position) {
        if(this.position == null)
            return false;

        return this.position.equals(position);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g, null);
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        if(image == null)
            return;

        image.draw(g, position.getScreenPosition(), observer);

        if(DebugLog.isOn())
            drawCollider(g, colliderColor);
    }

    private void drawCollider(Graphics2D g, Color color) {
        image.drawBorder(g, color, position);
    }

    @Override
    public String toString() {
        return position.toString();
    }

}
