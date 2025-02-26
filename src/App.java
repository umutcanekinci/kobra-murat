import javax.swing.*;

// JFrame -> Window class
// JPanel -> ?

public class App extends JFrame {

    public static String TITLE = "Kobra Murat";

    public App() {

        super(TITLE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Board stuffs
        Board board = new Board();
        add(board);
        addKeyListener(board);

        lock();
        pack();
        centerize();
        display();
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

    public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                new App();
             }
         }
         );
    }

}
