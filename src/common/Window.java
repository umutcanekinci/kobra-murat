package common;
import java.awt.Container;
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
        
        /*
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        lock();
        centerize();
        requestFocusInWindow();
        */
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case SERVER:
                break;
            case CLIENT:
                Game game = new Game();
                init(game, game);
                break;
            case EDITOR:
                Editor editor = new Editor();
                init(editor, editor);
                break;
        }
    }

    public void init(KeyListener keyListener, Container contentPane) {        
        addKeyListener(keyListener);
        setContentPane(contentPane);
    }

    private void fullScreen() {
        // System.setProperty("sun.java2d.uiScale.enabled", "true");
        // System.setProperty("sun.java2d.uiScale ", "2.0");

        setUndecorated(true);
        setPreferredSize(Constants.SIZE);
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
