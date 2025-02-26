import jdk.jshell.execution.Util;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;



public class Board extends JPanel implements ActionListener, KeyListener, GameListener{

    //region ---------------------------------------- ATTRIBUTES ------------------------------------------

    // DebugMode
    private boolean debugMode = true;
    private static final Rectangle DEBUG_RECT = new Rectangle(0, 0, 0, 0);

    // Player
    private final Player player;

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(232, 232, 232);
    private static final Color TILE_COLOR = new Color(214, 214, 214);

    // Timer
    public static int FPS = 60;
    public static double DELTATIME = 1.0 / FPS;
    public static int DELTATIME_MS = (int) (DELTATIME * 1000);

    // Size
    public static final int TILE_SIZE = 64;
    public static final int ROWS = 12;
    public static final int COLUMNS = 18;
    public static Dimension SIZE = new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);

    // Rects
    private static final Rectangle SCORE_RECT = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);

    // Apples
    private ArrayList<Apple> apples;
    public static final int APPLE_COUNT = 5;

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        setPreferredSize(SIZE);
        setBackground(BACKGROUND_COLOR);

        player = new Player();
        player.addListener(this);
        apples = populateApples();

        // this timer will call the actionPerformed() method every DELTATIME ms
        // keep a reference to the timer object that triggers actionPerformed() in
        // case we need access to it in another method
        Timer timer = new Timer(DELTATIME_MS, this);
        timer.start();
    }

    private ArrayList<Apple> populateApples() {
        ArrayList<Apple> appleList = new ArrayList<>();
        Random rand = new Random();

        // create the given number of apples in random positions on the board.
        // note that there is not check here to prevent two apples from occupying the same
        // spot, nor to prevent apples from spawning in the same spot as the player
        for (int i = 0; i < APPLE_COUNT; i++) {
            int appleX = rand.nextInt(COLUMNS);
            int appleY = rand.nextInt(ROWS);
            appleList.add(new Apple(appleX, appleY));
        }

        return appleList;
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_R) {
            restart();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F2) {
            debugMode = !debugMode;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    //endregion -

    //region ---------------------------------------- UPDATE METHODS --------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // this method is called by the timer every DELTATIME ms.
        // so this is the mainloop of the game

        player.update();
        collectApples();
        repaint();
    }

    private void collectApples() {
        apples.removeAll(GetCollectedApples());
    }

    private ArrayList<Apple> GetCollectedApples() {
        ArrayList<Apple> collectedApples = new ArrayList<>();

        for (Apple apple : apples) {
            if (apple.isCollide(player.getPos())) {
                player.grow(1);
                collectedApples.add(apple);
            }
        }
        return collectedApples;
    }

    private void restart() {
        player.reset();
        apples = populateApples();
    }

    //endregion

    //region ---------------------------------------- DRAW METHODS ----------------------------------------

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // when calling g.drawImage() we can use "this" for the ImageObserver
        // because Component implements the ImageObserver interface, and JPanel
        // extends from Component. So "this" Board instance, as a Component, can
        // react to imageUpdate() events triggered by g.drawImage()

        drawTiles(g);
        drawApples(g);
        drawPlayer(g);
        drawScore(g);

        if(debugMode) {
            drawDebug(g);
        }

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawTiles(Graphics g) {

        g.setColor(TILE_COLOR);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                // only color every other tile
                if ((row + col) % 2 == 1) {
                    // draw a square tile at the current row/column position
                    g.fillRect(
                            col * TILE_SIZE,
                            row * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );
                }
            }
        }
    }

    private void drawApples(Graphics g) {
        for (Apple apple : apples) {
            apple.draw(g, this);
        }
    }

    private void drawPlayer(Graphics g) {
        player.draw(g, this);
    }

    private void drawScore(Graphics g) {
        String text = "Score: " + player.getScore();
        Utils.DrawText(g, text, Color.GREEN, SCORE_RECT, true);
    }

    private void drawDebug(Graphics g) {
        String[] text = {
                "DEBUG MODE ON",
                "FPS: " + FPS,
                "Player Position: (" + player.getPos().x + ", " + player.getPos().y + ")",
        };

        for (String s : text) {
            DEBUG_RECT.y = drawDebugText(g, s, Color.BLACK);
        }
        DEBUG_RECT.y = drawDebugText(g, "Snake Length: " + player.getLength(), Color.GREEN);
        for (Point snakePart : player.getSnakeParts()) {
            DEBUG_RECT.y = drawDebugText(g, "Snake Part: (" + snakePart.x + ", " + snakePart.y + ")", Color.MAGENTA);
        }

        DEBUG_RECT.y = 0;
    }

    private int drawDebugText(Graphics g, String text, Color color) {
        return Utils.DrawText(g, text, color, DEBUG_RECT, false);
    }

    //endregion


    @Override
    public void onGameOver() {
        restart();
    }
}
