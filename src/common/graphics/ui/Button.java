package common.graphics.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import common.Utils;

import java.awt.Graphics2D;

public class Button extends ButtonGradient {

    public static final Dimension SIZE = new Dimension(700, 170);
    private static final Color COLOR = new Color(84, 148, 21);
    private static final Color COLOR2 = new Color(141, 198, 63);

    public Button(String text) {
        super();
        setText(text);
        setFont(new Font("Arial", Font.BOLD, 50));
        setRequestFocusEnabled(false);
        setColor1(COLOR);
        setColor2(COLOR2);
        setPreferredSize(Utils.scale(SIZE));
    }

    public Button(String text, ActionListener listener) {
        this(text);
        addActionListener(listener);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
                    listener.actionPerformed(null);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        //g2d.scale(Constants.SCALEW, Constants.SCALEH);
        super.paintComponent(g);
        //g2d.scale(1 / Constants.SCALEW, 1 / Constants.SCALEH);
        
    }
}
