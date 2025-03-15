package client;
import javax.swing.*;

public class Window extends JFrame {

    public static final String TITLE = "Kobra Murat";

    public Window() {

        super(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board board = new Board();
        add(board);
        addKeyListener(board);

        lock();
        pack();
        centerize();
        display();
        requestFocusInWindow();
    }

    private void lock() {
        setResizable(false);
    }

    private void centerize() {
        setLocationRelativeTo(null);
    }

    private void display() {
        setVisible(true);
    }

    public static void exit() {
        System.exit(0);
    }

    public static void start() {
        SwingUtilities.invokeLater(Window::new);
    }

}
