package editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.Constants;
import common.Position;
import common.Utils;

import client.graphics.UI;

public class Board extends JPanel implements ActionListener, KeyListener, MouseListener {

    //region ---------------------------------------- Variables ------------------------------------------

    private static final File MAP_FOLDER = new File(System.getProperty("user.dir") + "/maps");
    private static final String SAVE_FILE = "map.txt";
    private static final ArrayList<JButton> buttons = new ArrayList<>();

    private static Dimension SIZE;
    private static boolean doesMouseHold = false;
    private static GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    private static boolean isDrawing = false;
    private static int DRAW_MODE = 0;

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        setFullscreen();
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
        Tilemap.newMap(17, 30);
        hideWidgets();
        isDrawing = true;
    }

    private void onOpenButtonClick() {
        int[][] data = getMapData();
        if(data == null)
            return;
        
        Tilemap.load(data);
        hideWidgets();
        isDrawing = true;
    }

    private int[][] getMapData() {
        String uri = chooseFile(false);
        if(uri.isEmpty())
            return null;
            
        Path path = Paths.get(uri);
        if(!Files.exists(path))
            return null;
        
        String str = "";
        try {
            str = Files.readString(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(str.isEmpty())
            return null;
        
        return Utils.stringToData(str);
    }

    private String chooseFile(boolean isFolder) {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false); // disable the "All files" option. https://stackoverflow.com/questions/10083447/selecting-folder-destination-in-java
        chooser.setCurrentDirectory(MAP_FOLDER);

        if(isFolder) {
            chooser.setDialogTitle("Klasör Seç");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        else {
            chooser.setDialogTitle("Harita Seç");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
            chooser.setFileFilter(filter);
        }

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return "";

        File file = chooser.getSelectedFile();
        if(file == null)
            return "";

        return file.getAbsolutePath();
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
        new Timer(Constants.DELTATIME_MS, this).start();
    }

    //endregion

    //region ---------------------------------------- EVENT METHODS ---------------------------------------

    @Override
    public void mouseClicked(MouseEvent e) {}

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
        if(e.getKeyCode() == KeyEvent.VK_S)
            saveMap();
        
        if(!isDrawing) 
            keyPressedMenu(e);
        else 
            keyPressedGame(e);
    }

    private static void keyPressedMenu(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            exit();
    }

    private void keyPressedGame(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: {
                openMenu();
                break;
            }
            case KeyEvent.VK_D: {
                DRAW_MODE = -1;
                break;
            }
            case KeyEvent.VK_2: {
                DRAW_MODE = -2;
                break;
            }
            case KeyEvent.VK_0: {
                DRAW_MODE = 0;
                break;
            }

        }
    }

    private void saveMap() {
        int[][] data = Tilemap.getData();
        String str = Utils.dataToString(data);
        try {
            Files.write(Paths.get(chooseFile(true) + "/" + SAVE_FILE), str.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
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
        mouseTile = new Position(mouseTile.x / Constants.TILE_SIZE, mouseTile.y / Constants.TILE_SIZE);

        Tilemap.changeTile(mouseTile, DRAW_MODE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the screen
        setBackground(Color.BLACK);

        Graphics2D g2d = (Graphics2D) g;
        UI.init();
        
        if(isDrawing) {
            Tilemap.draw(g2d, this);
            drawGrid(g2d);
        }
        else
            UI.drawTitle(g2d, Constants.SIZE.width, 100, null);
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(Color.GRAY);
        
        for(int i=0; i<SIZE.width; i+=Constants.TILE_SIZE)
            g.drawLine(i, 0, i, SIZE.height);

        for(int i=0; i<SIZE.height; i+=Constants.TILE_SIZE)
            g.drawLine(0, i, SIZE.width, i);
    }
    
}
