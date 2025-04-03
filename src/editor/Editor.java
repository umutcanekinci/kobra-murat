package editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;
import java.awt.MouseInfo;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.Timer;

import common.Window;
import common.Constants;
import common.Position;
import common.Utils;
import common.graphics.Image;
import common.graphics.SplashEffect;

public class Editor extends JPanel implements ActionListener, KeyListener, MouseListener, UIListener {

    //region ---------------------------------------- Variables ------------------------------------------

    private static final File MAP_FOLDER = new File(System.getProperty("user.dir") + "/maps");
    private static final String SAVE_FILE = "map.txt";
    private static boolean doesMouseHold = false;

    private static int DRAW_MODE = 0;

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Editor() {
        super();
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        UI.init(this);
        initListeners();
        SplashEffect.start();
        initTimer();
    }

    private void initListeners() {
        addMouseListener(SplashEffect.getInstance());
        UI.addListener(this);
        SplashEffect.addListener(UI.getInstance());
    }

    @Override
    public void onNewButtonClicked() {
        Tilemap.newMap(17, 30);
        UI.MENU.openPage(Page.EDITOR);
    }

    @Override
    public void onOpenButtonClicked() {
        int[][] data = getMapData();
        if(data == null)
            return;
        
        Tilemap.load(data);
        UI.MENU.openPage(Page.EDITOR);
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

    public static void exit() {
        Window.exit();
    }

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
        SplashEffect.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_S)
            saveMap();
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            UI.MENU.goBack(event -> exit());

        if(UI.MENU.getCurrentPage() == Page.EDITOR)
            keyPressedEditor(e);
    }

    private void keyPressedEditor(KeyEvent e) {
        switch (e.getKeyCode()) {
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

        String uri = chooseFile(true);
        if(uri.isEmpty())
            return;

        Path path = Paths.get(uri + "/" + SAVE_FILE);
        if(!Files.exists(path.getParent()))
            return;

        try {
            Files.write(path, str.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); // Redraw the screen

        if(UI.MENU.getCurrentPage() == Page.EDITOR) {
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
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    private void draw(Graphics2D g) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        g.scale(Constants.SCALEW, Constants.SCALEH);
        
        if(SplashEffect.isPlaying()) {
            SplashEffect.draw(g, this);
            return;
        }

        UI.initGraphics(g);

        if(UI.MENU.getCurrentPage() == Page.MAIN_MENU) {
            Image.BACKGROUND_IMAGE.draw(g, 0, 0, this);
        }
        else
        {
            if(UI.MENU.getCurrentPage() == Page.EDITOR) {
                Tilemap.draw(g, this);
                Tilemap.drawGrid(g);
            }
        }
    }
    
}
