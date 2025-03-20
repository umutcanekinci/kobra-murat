package editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.graphics.UI;

import common.Position;
import common.Level;
import common.Utils;

public class Board extends JPanel implements ActionListener, KeyListener, MouseListener {

    //region ---------------------------------------- Variables ------------------------------------------

    public static Dimension SIZE;
    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);
    private static int DRAW_MODE = 0;
    private static boolean doesMouseHold = false;

    private static GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    private static boolean isDrawing = false;
    private static final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        setFullscreen();
        Tilemap.loadSheet();
        UI.init();
        initLayout();
        initWidgets();
        initTimer();
    }

    private void setFullscreen() {
        SIZE = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(SIZE);
    }

    private static void initLayout() {
        layout = new GridBagConstraints();
        layout.insets = new Insets(0, 10, 5, 0);
        layout.weighty = 1;
    }

    private void initWidgets() {
        addButton("Yeni", e -> onNewButtonClick());
        addButton("Aç", e -> onOpenButtonClick());
        addButton("Çıkış", e -> exit());
    }

    private void onNewButtonClick() {
        Tilemap.newMap(50, 70);
        hideWidgets();
        isDrawing = true;
    }

    private void onOpenButtonClick() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.showOpenDialog(this);

        File file = fileChooser.getSelectedFile();
        if(file == null)
            return;

        Path path = Paths.get(file.getAbsolutePath());
        if(!Files.exists(path))
            return;

        String str = "";
        try {
            str = Files.readString(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Tilemap.load(Utils.stringToData(str));
        hideWidgets();
        isDrawing = true;
    }

    private void addButton(String text, ActionListener listener) {
        JButton button = UI.newButton(text);
        button.addActionListener(listener);
        add(button, layout);
        buttons.add(button);
    }

    //region ---------------------------------------- BUTTON METHODS ----------------------------------------

    private static void hideWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(false);
        }
    }

    public static void exit() {
        Window.exit();
    }

    //endregion

    private void initTimer() {
        new Timer(DELTATIME_MS, this).start();
    }

    //endregion

    //region ---------------------------------------- EVENT METHODS ---------------------------------------

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3)
            DRAW_MODE = DRAW_MODE == -1 ? 0 : -1;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1)
            doesMouseHold = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1)
            doesMouseHold = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(!isDrawing) 
            keyPressedMenu(e);
        else 
            keyPressedGame(e);
    }

    private static void keyPressedMenu(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            exit();
    }

    private static void keyPressedGame(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: {
                openMenu();
        
                List<String> lines = Arrays.asList(Utils.dataToString(Tilemap.getData()).split("\n"));
                Path file = Paths.get("save.txt");
                try {
                    Files.write(file, lines, StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    ex.printStackTrace();   
                }
                //Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        }
    }
    
    public static void openMenu() {
        isDrawing = false;
        showWidgets();
    }

    private static void showWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(true);
        }
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); // Redraw the screen

        if(isDrawing) {
            if(doesMouseHold)
                paintTile();
        }
    }

    private void paintTile() {
        Position mouseTile = new Position(MouseInfo.getPointerInfo().getLocation());
        //mouseTile = SwingUtilities.convertPoint(null, mouseTile, this);
        mouseTile = new Position(mouseTile.x / Level.TILE_SIZE, mouseTile.y / Level.TILE_SIZE);

        Tilemap.changeTile(mouseTile, DRAW_MODE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the screen
        setBackground(Color.BLACK);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if(isDrawing) {
            Tilemap.draw(g2d, this);
            //Tilemap.drawColliders(g2d);
            drawGrid(g2d);
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(Color.GRAY);
        for(int i=0; i<SIZE.width; i+=Level.TILE_SIZE) {
            g.drawLine(i, 0, i, SIZE.height);
        }
        for(int i=0; i<SIZE.height; i+=Level.TILE_SIZE) {
            g.drawLine(0, i, SIZE.width, i);
        }
    }
    
}
