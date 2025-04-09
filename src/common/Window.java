package common;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;


import client.Game;
import editor.Editor;

public class Window extends JFrame {

    public enum Mode {
        SERVER,
        CLIENT,
        EDITOR;

        public String getTitle() {
            switch (this) {
                case SERVER:
                    return Constants.SERVER_TITLE;
                case CLIENT:
                    return Constants.TITLE;
                case EDITOR:
                    return Constants.EDITOR_TITLE;
                default:
                    return "Unknown Mode";
            }
        }
    }

    static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

    public static void open(Mode mode) {
        SwingUtilities.invokeLater(() -> {
            new Window(mode);
        });
    }

    public Window(Mode mode) {
        super(mode.getTitle());
        setMode(mode);
        fullScreen();  
        pack();
        display();
        setFocusable(true);
        requestFocusInWindow();
        
        /*
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        lock();
        centerize();
        */

    }

    public void setMode(Mode mode) {
        switch (mode) {
            case SERVER:
                break;
            case CLIENT:
                Game game = Game.getInstance();
                init(game, game, game);
                break;
            case EDITOR:
                Editor editor = new Editor();
                init(editor, editor, editor);
                break;
        }
    }

    public void init(KeyListener keyListener, Container contentPane, Component component) {        
        addKeyListener(keyListener);
        setContentPane(contentPane);

        // I don't know why, but this is not working when I add mouseListener to the JFrame.
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private void fullScreen() {
        // System.setProperty("sun.java2d.uiScale.enabled", "true");
        // System.setProperty("sun.java2d.uiScale ", "2.0");
        
        setPreferredSize(Constants.SCREEN_SIZE);
        
        // Windowed Fullscreen
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setUndecorated(true);
        

        //java.awt.Label label = new java.awt.Label("Kobra Murat", java.awt.Label.CENTER);
        //getContentPane().add(label);;

        // Fullscreen   
        setUndecorated(true);
        device.setFullScreenWindow(this);

    }

    /*
    private void lock() {
        setResizable(false);
    }

    private void centerize() {
        setLocationRelativeTo(null);
    }
    */

    private void display() {
        setVisible(true);
    }

    public static void exit() {
        System.exit(0);
    }

}
